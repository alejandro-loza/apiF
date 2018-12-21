package mx.finerio.api.services

import mx.finerio.api.domain.CredentialFailureMessage
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.CredentialFailureMessageRepository
import mx.finerio.api.domain.repository.BankConnectionRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.domain.BankConnection
import mx.finerio.api.domain.Credential

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CredentialFailureMessageService {

  @Autowired
  CredentialFailureMessageRepository credentialFailureMessageRepository

  @Value('${credentialFailure.defaultMessage}')
  String defaultMessage

  @Autowired
  BankConnectionRepository bankConnectionRepository

   @Autowired
  CustomErrorMessageService customErrorMessageService

  String findByInstitutionAndMessage( Credential credential,
      FinancialInstitution institution, String statusCode ) throws Exception {

    validateFindByInstitutionAndMessage( credential, institution, statusCode )

    def credentialFailureMessage
    def newStatusCode
    if( statusCode.equals("401" ) ){
      def bankConnection = bankConnectionRepository.findFirstByCredentialAndStatus(credential,BankConnection.Status.SUCCESS)
      if ( bankConnection ){
        newStatusCode='4011'
      }else{
        newStatusCode=statusCode
      }     
    }else{
        newStatusCode=statusCode
    }
 
   credentialFailureMessage = credentialFailureMessageRepository.findFirstByOriginalMessage( newStatusCode )
   customErrorMessageService.sendCustomEmail(credential.user?.username,newStatusCode)

    if ( !credentialFailureMessage ) {
      return defaultMessage
    } else {
      return credentialFailureMessage.friendlyMessage
    }

  }

  private void validateFindByInstitutionAndMessage(Credential credential,
      FinancialInstitution institution, String message ) throws Exception {

    if ( !credential ) {
      throw new BadImplementationException(
          'credentialFailureMessageService' +
          '.findByInstitutionAndMessage.credential.null' )
    }

    if ( !institution ) {
      throw new BadImplementationException(
          'credentialFailureMessageService' +
          '.findByInstitutionAndMessage.institution.null' )
    }

    if ( !message ) {
      throw new BadImplementationException(
          'credentialFailureMessageService' +
          '.findByInstitutionAndMessage.message.null' )
    }

  }

  private void create( FinancialInstitution institution, String message,
      String friendlyMessage ) throws Exception {

    def credentialFailureMessage = new CredentialFailureMessage()
    credentialFailureMessage.institution = institution
    credentialFailureMessage.originalMessage = message.take( 200 )
    credentialFailureMessage.friendlyMessage = friendlyMessage.take( 50 )
    credentialFailureMessage.dateCreated = new Date()
    credentialFailureMessageRepository.save( credentialFailureMessage )

  }

}
