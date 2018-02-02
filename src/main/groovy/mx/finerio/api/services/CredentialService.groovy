package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException
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
  ConfigService configService

  void requestData( String credentialId ) throws Exception {

    def credential = findAndValidate( credentialId, 'requestData' )
    credential.providerId = 3L
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

  def createCredential( CredentialDto credential ) throws Exception {

    def institution = financialInstitutionService.findById( credential.institution.id )
    if ( !institution ) {
      throw new IllegalArgumentException(
          'credential.createCredential.institution.null' )
    }
    def user = userService.findById( credential.user.id )
    if ( !user ) {
      throw new IllegalArgumentException(
          'credential.createCredential.user.null' )
    }
    def cred = credentialRepository.findByUserAndInstitutionAndUsername( user, institution, credential.username )
    if ( cred ) {
      throw new Exception(
          'credential.createCredential.credential.exist' )
    }else{
      cred = new Credential()
      cred.institution = institution
      cred.user = user

      def key = configService.findByItem( Config.Item.CRYPT_KEY  )
      cryptService.updateKey( key )

      def cryp = cryptService.encrypt( credential.password )
      cred.password = cryp.message
      cred.iv = cryp.iv

      cred.securityCode = credential.securityCode
      cred.username = credential.username
      cred.status = Credential.Status.VALIDATE
      cred.lastUpdated = credential.lastUpdated ?: new Date()
      cred.dateCreated = credential.dateCreated ?: new Date()
      cred.version = 0 
      credentialRepository.save( cred )
    }
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
