package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface FinancialInstitutionRepository extends JpaRepository<FinancialInstitution, Long>, JpaSpecificationExecutor {
  
  FinancialInstitution findById( Long id )
  FinancialInstitution findByCodeAndCustomerAndDateDeletedIsNull(String code, Customer customer )
  FinancialInstitution findByIdAndCustomerAndDateDeletedIsNull(Long id, Customer)

}
