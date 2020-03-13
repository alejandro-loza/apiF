package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface BankFieldRepository extends JpaRepository<BankField, Long>, JpaSpecificationExecutor {
  
  List<BankField> findAllByProviderIdAndFinancialInstitutionAndInteractiveIsFalse( Long providerId, FinancialInstitution financialInstitution )

}
