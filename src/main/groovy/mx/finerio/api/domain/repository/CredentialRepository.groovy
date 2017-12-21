package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.Credential

interface CredentialRepository extends JpaRepository<Credential, Long>, JpaSpecificationExecutor {
  

}
