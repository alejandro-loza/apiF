package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface AccountCredentialRepository extends JpaRepository<AccountCredential, Long>, JpaSpecificationExecutor {
  
  AccountCredential findByAccountAndCredential(Account account, Credential credential)

  List<AccountCredential> findByCredential( Credential credential )

}
