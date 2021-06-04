package mx.finerio.api.services

import groovy.json.JsonBuilder
import mx.finerio.api.domain.FinancialInstitution.Status
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.domain.repository.*
import mx.finerio.api.domain.*
import mx.finerio.api.domain.FinancialInstitution.Provider
import mx.finerio.api.dtos.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import mx.finerio.api.services.AdminService.EntityType
import java.time.LocalDate
import java.time.format.DateTimeParseException
import mx.finerio.api.dtos.CreateCredentialSatwsDto

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
  ScraperService scraperService

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
  SecurityService securityService

  @Autowired
  WidgetEventsService widgetEventsService

  @Value( '${sync.user.name}' )
  String syncUsername
  
  @Autowired
  SignalRService signalRService

  @Autowired
  AdminService adminService

  @Autowired
  CredentialStateService credentialStateService
  
  @Autowired
  ClientWidgetRepository clientWidgetRepository

  @Autowired
  ScraperV2Service scraperV2Service
  
  @Autowired
  ScraperV2TokenService scraperV2TokenService

  @Autowired
  CallbackGatewayClientService callbackGatewayClientService

  @Value('${gateway.source}')
  String source

  @Value('${scraperv2.rangeDates.monthsAgo}') 
  int monthsAgo

  @Autowired
  SatwsService satwsService

  
  Credential create( CredentialDto credentialDto, Customer customer = null, Client client = null ) throws Exception {

    if ( !credentialDto ) {
      throw new BadImplementationException(
        'credentialService.create.credentialDto.null' )
    }
      
    if( !customer ){
      customer = customerService.findOne( credentialDto.customerId )
    }
    def bank = financialInstitutionService.findOneAndValidate(
        credentialDto.bankId )
    def instance = credentialRepository.
        findByCustomerAndInstitutionAndUsernameAndDateDeleted(
            customer, bank, credentialDto.username, null )
    def instanceExists = instance != null

    if ( !instanceExists ) {
      def data = [ customer: customer, bank: bank, credentialDto: credentialDto ]
      instance = createInstance( data )
    }


    if( credentialDto.state ) {
      credentialStateService.save( instance.id, credentialDto.state )
    }
    def rangeDates = getRangeDates( credentialDto )
    requestData( instance.id, rangeDates, client )

    if ( !instanceExists ) {
      adminService.sendDataToAdmin( EntityType.CREDENTIAL, instance )
    }
    widgetEventsService.onCredentialCreated( new WidgetEventsDto(
        credentialId: instance.id ) )
    instance

  }
  
  private Map getRangeDates( CredentialRangeDto credentialRangeDto ) {

    def dates = [:]

    if( credentialRangeDto.startDate && credentialRangeDto.endDate ){

      if( !isValidDate( credentialRangeDto.startDate ) ){
        throw new BadImplementationException(
        'credentialService.getRangeDates.startDate.wrongFormat' )
      }
      if( !isValidDate( credentialRangeDto.endDate ) ){
        throw new BadImplementationException(
        'credentialService.getRangeDates.endDate.wrongFormat' )
      }

      if( LocalDate.parse( credentialRangeDto.startDate)
          .isAfter( LocalDate.parse( credentialRangeDto.endDate )) ){
        throw new BadImplementationException(
        'credentialService.getRangeDates.dates.wrongRange' )
      }

      dates.startDate = credentialRangeDto.startDate
      dates.endDate = credentialRangeDto.endDate      
    
    }else{
      
      LocalDate now = LocalDate.now()   
      dates.endDate = now.toString()   
      dates.startDate = now.minusMonths( monthsAgo ).toString()

    }      
   
    dates

  }

  private boolean isValidDate( String dateStr ) {
    try {
        LocalDate.parse(dateStr)
    } catch (DateTimeParseException e) {
        return false
    }
     true
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

  Credential findOne( String id, Client client = null ) throws Exception {

    if ( !id ) {
      throw new BadImplementationException(
          'credentialService.findOne.id.null' )
    }
 
    if( !client ){
      client = securityService.getCurrent()
    }
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
  
  Credential findByCustomerAndFinancialIntitution( Customer customer, FinancialInstitution financialInstitution ) throws Exception {

   if ( !customer ) {
      throw new BadImplementationException(
          'credentialService.findByCustomerAndFinancialIntitution.customer.null' )
   }

   if ( !financialInstitution ) {
      throw new BadImplementationException(
          'credentialService.findByCustomerAndFinancialIntitution.financialInstitution.null' )
   }

    def credential = credentialRepository
      .findByCustomerAndInstitutionAndDateDeletedIsNull( customer, financialInstitution )

    if ( !credential ) {
      throw new InstanceNotFoundException( 'credential.not.found' )
    }
    credential
  }


  Credential findByScrapperCredentialIdAndInstitution(
     String scrapperCredentialId, FinancialInstitution financialInstitution ) throws Exception {

    if ( !scrapperCredentialId ) {
      throw new BadImplementationException(
          'credentialService.findByCustomerAndFinancialIntitution.scrapperCredentialId.null' )
   }

   if ( !financialInstitution ) {
      throw new BadImplementationException(
          'credentialService.findByCustomerAndFinancialIntitution.financialInstitution.null' )
   }

    def credential = credentialRepository
      .findByScrapperCredentialIdAndInstitutionAndDateDeletedIsNull( scrapperCredentialId, financialInstitution )

    if ( !credential ) {
      throw new InstanceNotFoundException( 'credential.not.found' )
    }
    
    credential
  }

   Credential findByInstitutionAndUsername( 
      FinancialInstitution financialInstitution, String username ) throws Exception {

      if ( !financialInstitution ) {
      throw new BadImplementationException(
          'credentialService.findByInstitutionAndUsername.financialInstitution.null' )
   }

    if ( !username ) {
      throw new BadImplementationException(
          'credentialService.findByInstitutionAndUsername.username.null' )
   }

    def credential = credentialRepository
      .findByInstitutionAndUsernameAndDateDeletedIsNull( financialInstitution, username )

    if ( !credential ) {
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
    def instance = findOne( id, credentialUpdateDto.client )
    financialInstitutionService.findOneAndValidate( instance.institution.id )

    if ( credentialUpdateDto.securityCode ) {
      instance.securityCode = credentialUpdateDto.securityCode
    }

    if ( credentialUpdateDto.password ) {

      def encryptedData = cryptService.encrypt( credentialUpdateDto.password )
      instance.password = encryptedData.message
      instance.iv = encryptedData.iv

    }

    if ( credentialUpdateDto.automaticFetching != null ) {
      instance.automaticFetching = credentialUpdateDto.automaticFetching
    }

    instance.lastUpdated = new Date()
    instance = credentialRepository.save( instance )
    def rangeDates = getRangeDates( credentialUpdateDto )

    if ( credentialUpdateDto.automaticFetching != false &&
        ( instance.status != Credential.Status.VALIDATE ) &&
        ( !credentialRecentlyUpdated( instance ) ) ) {
      requestData( instance.id, rangeDates, credentialUpdateDto.client )
    }

    instance

  }

  void requestData( String credentialId, Map rangeDates = null, Client client = null ) throws Exception {
    def credential = findOne( credentialId, client )
    if ( credentialRecentlyUpdated( credential ) ) { return }
    credential.status = Credential.Status.VALIDATE
    credential.providerId = 3L
    credential.errorCode = null
    credential.lastUpdated = new Date()
    credentialRepository.save( credential )
    if ( credential.institution.status == FinancialInstitution.Status.PARTIALLY_ACTIVE ) {
      scraperCallbackService.processSuccess(
              SuccessCallbackDto.getInstanceFromCredentialId( credential.id ) )
      scraperCallbackService.postProcessSuccess( credential )
      return
    }
    bankConnectionService.create( credential )
    credentialStatusHistoryService.create( credential )

    if( !rangeDates ) {
      rangeDates = getRangeDates( new CredentialRangeDto() )
    }

    def provider = credential.institution.provider

    switch( provider ) {
      case Provider.SCRAPER_V2:
        sendToScraperV2( credential, rangeDates )
      break
      case Provider.SCRAPER_V1:
        sendToScraperV2LegacyPayload( credential, rangeDates )
      break
      case Provider.SATWS:      
        sendToSatws( credential )
      break
      default:
        throw new BadImplementationException(
            'credentialService.requestData.wrong.provider' )

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

  Credential updateProviderId( String credentialId, String providerId )
      throws Exception {
        
    def credential = findAndValidate( credentialId )
    credential.scrapperCredentialId = providerId
    credentialRepository.save( credential )        
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
        status: credential.status,
        automaticFetching: credential.automaticFetching,
        dateCreated: credential.dateCreated ]

  }
  @Transactional
  void processInteractiveWidget(
      CredentialInteractiveWidgetDto credentialInteractiveWidgetDto ) throws Exception {

    if ( !credentialInteractiveWidgetDto ) {
      throw new BadRequestException(
        'credentialService.processInteractiveWidget.credentialInteractiveWidgetDto.null' )
      }

    ClientWidget clientWidget = clientWidgetRepository
                  .findByWidgetId( credentialInteractiveWidgetDto.widgetId )

     if( !clientWidget ){
      throw new BadRequestException(
          'credentialService.processInteractiveWidget.credentialInteractiveWidgetDto.widgetId.notFound' )
    }

    def client = clientWidget.client 
    def credentialId = credentialInteractiveWidgetDto.id

    def credential = findOne( credentialId, client )

   processInteractive( credentialId, 
      new CredentialInteractiveDto( token: credentialInteractiveWidgetDto.token ), client )

  }
  
  void processInteractive( String id,
      CredentialInteractiveDto credentialInteractiveDto, Client client = null ) throws Exception {

    if ( !credentialInteractiveDto ) {
      throw new BadRequestException(
          'credentialService.processInteractive.credentialInteractiveDto.null' )
    }
    
    def credential
    if( client ) {
     credential = findOne( id, client )
    }else{ 
      credential = findOne( id ) 
    }
     
    def institutionCode = credential.institution.code

    if( ![ 'BAZ','BBVA','BANORTE' ].contains( institutionCode ) ) {
      throw new BadRequestException(
        'credentialService.processInteractive.institutionCode.wrong' )
    }

    scraperV2TokenService.send( credentialInteractiveDto.token, id, institutionCode )
    widgetEventsService.onCredentialCreated( new WidgetEventsDto(
        credentialId: credential.id ) )

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
    instance.automaticFetching = credentialDto.automaticFetching
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

    def institutionCode = credential.institution.code
    if( [ 'BAZ','BANORTE' ].contains( institutionCode ) ) {
      callbackGatewayClientService
        .registerCredential( [ credentialId: credential.id ,source: source ] )
    }

    scraperService.requestData( data )
  }

  private void sendToScraperV2(  Credential credential, Map rangeDates  ) {

    def plainPassword = cryptService.decrypt( credential.password,
        credential.iv )
 
   def dto = new CreateCredentialDto(
     bankCode: credential.institution.internalCode,
     username: credential.username,
     password: plainPassword,
     credentialId: credential.id,
     startDate: rangeDates.startDate,
     endDate: rangeDates.endDate
  )

  scraperV2Service.createCredential( dto ) 

 }


  private void sendToScraperV2LegacyPayload( Credential credential, Map rangeDates ) throws Exception {
 
    def data = [
      id: credential.id,
      username: credential.username,
      password: credential.password,
      iv: credential.iv,
      user: [ id: credential.user.id ],
      institution: [ id: "${credential.institution.id}" ],
      securityCode: credential.securityCode,
      startDate: rangeDates.startDate,
      endDate: rangeDates.endDate,
    ]

    def institutionCode = credential.institution.code
    if( [ 'BAZ','BANORTE' ].contains( institutionCode ) ) {
      callbackGatewayClientService
        .registerCredential( [ credentialId: credential.id ,source: source ] )
    }

    scraperV2Service.createCredentialLegacyPayload( data )
  }


  private void sendToSatws( Credential credential ) throws Exception {

    def plainPassword = cryptService.decrypt( credential.password,
        credential.iv ) 

     def dto = new CreateCredentialSatwsDto(     
      rfc: credential.username,
      password: plainPassword,
      credentialId: credential.id
    )
    def credentialProviderId = satwsService.createCredential( dto )
    credential.scrapperCredentialId = credentialProviderId
    credentialRepository.save( credential )
        
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
