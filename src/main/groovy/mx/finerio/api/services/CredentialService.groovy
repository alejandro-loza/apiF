package mx.finerio.api.services

import javax.validation.Valid

import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.domain.repository.*
import mx.finerio.api.domain.*
import mx.finerio.api.dtos.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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

  Credential create( @Valid CredentialDto credentialDto ) throws Exception {

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
    createInstance( data )

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

  private Credential findAndValidate( String id, String method )
      throws Exception {

    def credential = credentialPersistenceService.findOne( id )

    if ( !credential ) {
      throw new InstanceNotFoundException(
          "credential.${method}.credential.null" )
    }

    credential

  }

}
