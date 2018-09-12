package mx.finerio.api.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface AccountRepository extends JpaRepository<Account, String >, JpaSpecificationExecutor {
  
  Account findFirstByInstitutionAndUserAndNumberOrderByDateCreatedDesc(FinancialInstitution financialInstitution, User user, String number)
  Account findByInstitutionAndUserAndNameAndDeleted(FinancialInstitution financialInstitution, User user, String name, boolean deleted) 
  Account findFirstByInstitutionAndUserAndNumberLikeOrderByDateCreatedDesc(FinancialInstitution financialInstitution, User user, String number)
  Account findById( String id )
  List<Account> findByUserAndDeleted( User user, boolean deleted )

  @Query( value= "select ac.account from AccountCredential ac where ac.credential = ?1 " )
  Page<Account> getByCredentialId( Credential credentiaId, Pageable pageable )

}
