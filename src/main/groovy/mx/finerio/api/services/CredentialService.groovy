package mx.finerio.api.services

import groovy.json.JsonBuilder

import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.domain.repository.*
import mx.finerio.api.domain.*
import mx.finerio.api.dtos.*
import mx.finerio.api.dtos.ScraperWebSocketSendDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import mx.finerio.api.services.AdminService.EntityType

@Service
class CredentialService {

  @Autowired
  AccountService accountService

  @Autowired
  BankConnectionService bankConnectionService

  @Autowired
  CredentialFailureMessageService credentialFailureMessageService

  @Autowired
  CredentialStatusHistoryService credentialStatusHistoryService

  @Autowired
  CustomerService customerService

  @Autowired
  DevScraperService scraperService

  @Autowired
  CredentialRepository credentialRepository

  @Autowired
  UserService userService

  @Autowired
  FinancialInstitutionService financialInstitutionService

  @Autowired
  CryptService cryptService

  @Autowired
  ListService listService

  @Autowired
  ScraperCallbackService scraperCallbackService

  @Autowired
  ScraperWebSocketService scraperWebSocketService

  @Autowired
  SecurityService securityService

  @Value( '${sync.user.name}' )
  String syncUsername
  
  @Autowired
  SignalRService signalRService

  @Autowired
  AdminService adminService

  Credential create( CredentialDto credentialDto ) throws Exception {

    if ( !credentialDto ) {
      throw new BadImplementationException(
          'credentialService.create.credentialDto.null' )
    }
 
    def customer = customerService.findOne( credentialDto.customerId )
    def bank = financialInstitutionService.findOneAndValidate(
        credentialDto.bankId )
    def existingInstance = credentialRepository.
        findByCustomerAndInstitutionAndUsernameAndDateDeleted(
            customer, bank, credentialDto.username, null )

    if ( existingInstance ) {
      throw new BadRequestException( 'credential.create.exists' )
    }

    def data = [ customer: customer, bank: bank, credentialDto: credentialDto ]
    def instance = createInstance( data )
    requestData( instance.id )
    adminService.sendDataToAdmin( EntityType.CREDENTIAL, instance )
    instance

  }

  Map findAll( Map params ) throws Exception {

    if ( params == null ) {
      throw new BadImplementationException(
          'credentialService.findAll.params.null' )
    }
 
    def dto = getFindAllDto( params )
    def spec = CredentialSpecs.findAll( dto )
    listService.findAll( dto, credentialRepository, spec )

  }

  Credential findOne( String id ) throws Exception {

    if ( !id ) {
      throw new BadImplementationException(
          'credentialService.findOne.id.null' )
    }
 
    def client = securityService.getCurrent()
    def instance = credentialRepository.findOne( id )

    if ( instance && client.username == syncUsername ) {
      return instance
    }

    if ( !instance || instance?.customer?.client?.id != client.id ||
        instance.dateDeleted ) {
      throw new InstanceNotFoundException( 'credential.not.found' )
    }

    instance

  }

  @Transactional 
  Credential findAndValidate( String id ) throws Exception {

    if ( !id ) {
      throw new BadImplementationException(
          'credentialService.findAndValidate.id.null' )
    }

    def credential = credentialRepository.findOne( id )

    if ( !credential || credential.dateDeleted ) {
      throw new InstanceNotFoundException( 'credential.not.found' )
    }

    credential

  }

  Credential validateUserCredential( Credential credential, String userId ) throws Exception {
  
    if ( !credential ) {
      throw new BadImplementationException(
          'credentialService.validateUserCredential.credential.null' )
    }
    if ( !userId ) {
      throw new BadImplementationException(
          'credentialService.validateUserCredential.userId.null' )
    }
    def user = userService.findById( userId )
    if ( !user ) {
      throw new InstanceNotFoundException( 'user.not.found' )
    }
    if( credential.user != user ){ return null }
    return credential
    

  }

  Credential update( String id, CredentialUpdateDto credentialUpdateDto
      ) throws Exception {

    validateUpdateInput( id, credentialUpdateDto )
    def instance = findOne( id )
    if ( instance.status == Credential.Status.VALIDATE ) { return instance }
    if ( credentialRecentlyUpdated( instance ) ) { return instance }
    financialInstitutionService.findOneAndValidate( instance.institution.id )

    if ( credentialUpdateDto.securityCode ) {
      instance.securityCode = credentialUpdateDto.securityCode
    }

    if ( credentialUpdateDto.password ) {

      def encryptedData = cryptService.encrypt( credentialUpdateDto.password )
      instance.password = encryptedData.message
      instance.iv = encryptedData.iv

    }

    instance.lastUpdated = new Date()
    instance = credentialRepository.save( instance )
    requestData( instance.id )
    instance

  }

  void requestData( String credentialId ) throws Exception {

    def credential = findOne( credentialId )
    if ( credentialRecentlyUpdated( credential ) ) { return }
    credential.status = Credential.Status.VALIDATE
    credential.providerId = 3L
    credential.errorCode = null
    credential.lastUpdated = new Date()
    credentialRepository.save( credential )
    bankConnectionService.create( credential )
    credentialStatusHistoryService.create( credential )

    if ( credential.institution.code == 'BBVA' ) {
      sendToScraperWebSocket( credential )
    }else{
	    sendToScraper( credential )
    }

  }

  Credential updateStatus( String credentialId, Credential.Status status )
      throws Exception {

    if ( !status ) {
      throw new BadImplementationException(
          'credentialService.updateStatus.status.null' )
    }

    def credential = findAndValidate( credentialId )
    credential.status = status
    credentialRepository.save( credential )
    bankConnectionService.update( credential, BankConnection.Status.SUCCESS )
    credential

  }

  Credential setFailure( String credentialId, String statusCode ) throws Exception {

    def credential = findAndValidate( credentialId )
    credential.errorCode = credentialFailureMessageService.
    findByInstitutionAndMessage( credential, credential.institution, statusCode ?: 'BLANK MSG' )
    credential.status = Credential.Status.INVALID
    credential.statusCode = statusCode
    credentialRepository.save( credential )
    bankConnectionService.update( credential, BankConnection.Status.FAILURE )
    
    credential

  }

  Map getFields( Credential credential ) throws Exception {

    if ( !credential ) {
      throw new BadImplementationException(
          'credentialService.getFields.credential.null' )
    }
 
    [ id: credential.id, username: credential.username,
        status: credential.status, dateCreated: credential.dateCreated ]

  }

  void processInteractive( String id,
      CredentialInteractiveDto credentialInteractiveDto ) throws Exception {

    if ( !credentialInteractiveDto ) {
      throw new BadImplementationException(
          'credentialService.processInteractive.credentialInteractiveDto.null' )
    }
 
    def credential = findOne( id )
	
	   if( credential.institution.code == 'BAZ' || credential.institution.code == 'BANORTE' ) {
	       signalRService.sendTokenToScrapper( credentialInteractiveDto.token, id )
		 return
	   }
		
    def data = [ data: [
      stage: 'interactive',
      id: credential.id,
      user_id: credential.user.id,
      otp: credentialInteractiveDto.token
    ] ]
    scraperWebSocketService.send( new ScraperWebSocketSendDto(
        id: credential.id,
        message: new JsonBuilder( data ).toString(),
        tokenSent: true,
        destroyPreviousSession: false ) )

  }

  void delete( String id ) throws Exception {

    def instance = findOne( id )
    accountService.deleteAllByCredential( instance )
    instance.dateDeleted = new Date()
    credentialRepository.save( instance )

  }

  private Credential createInstance( Map data ) throws Exception {

    def credentialDto = data.credentialDto
    def instance = new Credential()
    instance.customer = data.customer
    instance.institution = data.bank
    instance.user = userService.getApiUser()
    instance.username = credentialDto.username
    def encryptedData = cryptService.encrypt( credentialDto.password )
    instance.password = encryptedData.message
    instance.iv = encryptedData.iv
    instance.securityCode = credentialDto.securityCode
    instance.status = Credential.Status.VALIDATE
    def now = new Date()
    instance.dateCreated = now
    instance.lastUpdated = now
    instance.version = 0
    instance.providerId = 3L
    credentialRepository.save( instance )

  }

  private CredentialListDto getFindAllDto( Map params ) throws Exception {

    if ( !params.customerId ) {
      throw new BadRequestException( 'credential.findAll.customerId.null' )
    }

    def dto = new CredentialListDto()

    try {
      dto.customer = customerService.findOne( params.customerId as Long )
    } catch ( NumberFormatException e ) {
      throw new BadRequestException( 'credential.findAll.customerId.invalid' )
    }

    listService.validateFindAllDto( dto, params )

    if ( params.cursor ) {
      def cursorInstance = findOne( params.cursor )
      dto.dateCreated = cursorInstance.dateCreated
    }

    dto

  }

  private void validateUpdateInput( String id,
      CredentialUpdateDto credentialUpdateDto ) throws Exception {

    if ( !id ) {
      throw new BadImplementationException(
          'credentialService.update.id.null' )
    }

    if ( !credentialUpdateDto ) {
      throw new BadImplementationException(
          'credentialService.update.credentialUpdateDto.null' )
    }

  }

  private void sendToScraper( Credential credential ) throws Exception {

    def data = [
      id: credential.id,
      username: credential.username,
      password: credential.password,
      iv: credential.iv,
      user: [ id: credential.user.id ],
      institution: [ id: credential.institution.id ],
      securityCode: credential.securityCode
    ]
    scraperService.requestData( data )

  }

  private void sendToScraperWebSocket( Credential credential )
      throws Exception {

    def data = [ data: [
      stage: 'start',
      id: credential.id,
      tarjeta: credential.username,
      password: credential.password,
      iv: credential.iv,
      user_id: credential.user.id
    ] ]
    scraperWebSocketService.send( new ScraperWebSocketSendDto(
        id: credential.id,
        message: new JsonBuilder( data ).toString(),
        tokenSent: false,
        destroyPreviousSession: true ) )

  }
  

  private boolean credentialRecentlyUpdated( Credential credential )
      throws Exception {

    def bankConnection = bankConnectionService.findLast( credential )
    if ( !bankConnection ) { return false }
    def cal = Calendar.instance
    cal.time = new Date()
    cal.add( Calendar.HOUR, -8 )
    if ( bankConnection.startDate <= cal.time ) { return false }

    if ( bankConnection.status == BankConnection.Status.FAILURE ) {
      return false
    }

    true

  }

}
