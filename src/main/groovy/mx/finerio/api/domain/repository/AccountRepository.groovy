package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor {
  
  Account findByInstitutionAndUserAndNumberAndDeleted(FinancialInstitution financialInstitution, User user, String number, boolean deleted)
  Account findByInstitutionAndUserAndNameAndDeleted(FinancialInstitution financialInstitution, User user, String name, boolean deleted) 
  Account findByInstitutionAndUserAndNumberLikeAndDeleted(FinancialInstitution financialInstitution, User user, String number, boolean deleted)  
  Account findById( String id )
}
