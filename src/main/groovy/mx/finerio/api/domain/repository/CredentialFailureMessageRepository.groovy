package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository

import mx.finerio.api.domain.CredentialFailureMessage
import mx.finerio.api.domain.FinancialInstitution

interface CredentialFailureMessageRepository
    extends JpaRepository<CredentialFailureMessage, Long> {
  
  CredentialFailureMessage findFirstByInstitutionAndOriginalMessage(
      FinancialInstitution institution, String originalMessage )

}
