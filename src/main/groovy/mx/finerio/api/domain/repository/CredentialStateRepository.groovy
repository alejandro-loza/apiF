package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.CredentialState

interface CredentialStateRepository extends JpaRepository<CredentialState, Long>,
    JpaSpecificationExecutor {
  
  CredentialState findByCredentialId( String credentialId )
  
}
