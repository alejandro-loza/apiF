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

  String findByInstitutionAndMessage( Credential credential,
      FinancialInstitution institution, String statusCode ) throws Exception {

    validateFindByInstitutionAndMessage( institution, statusCode )

    def credentialFailureMessage
    if( statusCode.equals("401" ) ){
      def bankConnection = bankConnectionRepository.findFirstByCredentialAndStatus(credential,BankConnection.Status.SUCCESS)
      if ( bankConnection ){
        credentialFailureMessage = credentialFailureMessageRepository.findFirstByOriginalMessage( '4011' )
      }else{
        credentialFailureMessage = credentialFailureMessageRepository.findFirstByOriginalMessage( statusCode ) 
      }     
    }else{
        credentialFailureMessage = credentialFailureMessageRepository.findFirstByOriginalMessage( statusCode )
    }

    if ( !credentialFailureMessage ) {
      return defaultMessage
    } else {
      return credentialFailureMessage.friendlyMessage
    }

  }

  private void validateFindByInstitutionAndMessage(
      FinancialInstitution institution, String message ) throws Exception {

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
