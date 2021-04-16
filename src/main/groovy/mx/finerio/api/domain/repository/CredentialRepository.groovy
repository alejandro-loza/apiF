package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface CredentialRepository extends JpaRepository<Credential, Long>, JpaSpecificationExecutor {

  Credential findByCustomerAndInstitutionAndUsernameAndDateDeleted(
      Customer customer, FinancialInstitution institution, String username,
      Date dateDeleted )

  Credential findByCustomerAndInstitutionAndDateDeletedIsNull(
      Customer customer, FinancialInstitution institution )     
}
