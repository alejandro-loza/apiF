package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface CredentialStatusHistoryRepository extends JpaRepository<CredentialStatusHistory, Long>, JpaSpecificationExecutor {

  List findAllByCredentialAndDateDeletedIsNull( Credential credential )

}
