package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.CredentialToken

interface CredentialTokenRepository extends JpaRepository<CredentialToken, Long>,
    JpaSpecificationExecutor {
  
  CredentialToken findByCredentialId(String credentialId)
  
}
