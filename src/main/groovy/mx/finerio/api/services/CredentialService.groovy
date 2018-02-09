package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.domain.repository.*
import mx.finerio.api.domain.*
import mx.finerio.api.dtos.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CredentialService {

  @Autowired
  CredentialPersistenceService credentialPersistenceService

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
  @Lazy
  CredentialService selfReference

  @Autowired
  ListService listService

  @Autowired
  SecurityService securityService

  Credential create( CredentialDto credentialDto ) throws Exception {

    if ( !credentialDto ) {
      throw new BadImplementationException(
          'credentialService.create.credentialDto.null' )
    }
 
    def customer = customerService.findOne( credentialDto.customerId )
    def bank = financialInstitutionService.findOne( credentialDto.bankId )

    def existingInstance =
        credentialRepository.findByCustomerAndInstitutionAndUsername(
            customer, bank, credentialDto.username )

    if ( existingInstance ) {
      throw new BadRequestException( 'credential.create.exists' )
    }

    def data = [ customer: customer, bank: bank, credentialDto: credentialDto ]
    def instance = createInstance( data )
    selfReference.asyncRequestData( instance.id )
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

    if ( !instance || instance?.customer?.client?.id != client.id ) {
      throw new InstanceNotFoundException( 'credential.not.found' )
    }
 
    instance

  }

  Credential update( String id, CredentialUpdateDto credentialUpdateDto
      ) throws Exception {

    validateUpdateInput( id, credentialUpdateDto )
    def instance = findOne( id )

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
    selfReference.asyncRequestData( instance.id )
    instance

  }

  void requestData( String credentialId ) throws Exception {

    def credential = findAndValidate( credentialId, 'requestData' )
    credential.providerId = 3L
    credential.errorCode = null
    credentialRepository.save( credential )
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

  void updateStatus( String credentialId, Credential.Status status )
      throws Exception {

    def credential = findAndValidate( credentialId, 'updateStatus' )

    if ( !status ) {
      throw new BadImplementationException(
          'credentialService.updateStatus.status.null' )
    }

    credential.status = status
    credentialRepository.save( credential )

  }

  void setFailure( String credentialId, String message ) throws Exception {

    def credential = findAndValidate( credentialId, 'setFailure' )

    if ( message == null ) {
      throw new BadImplementationException(
          'credentialService.setFailure.message.null' )
    }

    credential.errorCode = message.take( 255 )
    credential.status = Credential.Status.INVALID
    credentialRepository.save( credential )

  }

  Map getFields( Credential credential ) throws Exception {

    if ( !credential ) {
      throw new BadImplementationException(
          'credentialService.getFields.credential.null' )
    }
 
    [ id: credential.id, username: credential.username,
        status: credential.status, dateCreated: credential.dateCreated ]

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

  private Credential findAndValidate( String id, String method )
      throws Exception {

    def credential = credentialPersistenceService.findOne( id )

    if ( !credential ) {
      throw new InstanceNotFoundException(
          "credential.${method}.credential.null" )
    }

    credential

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

  @Async
  @Transactional
  void asyncRequestData( String credentialId ) throws Exception {
    requestData( credentialId )
  }

}
