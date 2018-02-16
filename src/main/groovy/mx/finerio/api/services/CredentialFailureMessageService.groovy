package mx.finerio.api.services

import mx.finerio.api.domain.CredentialFailureMessage
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.CredentialFailureMessageRepository
import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CredentialFailureMessageService {

  @Autowired
  CredentialFailureMessageRepository credentialFailureMessageRepository

  CredentialFailureMessage findByInstitutionAndMessage(
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

    credentialFailureMessageRepository
        .findFirstByInstitutionAndOriginalMessage( institution, message )

  }

}
