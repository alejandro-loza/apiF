package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails, Long>, JpaSpecificationExecutor {
  
  OauthClientDetails findOneByClientId( String clientId )
  OauthClientDetails findOneByAdditionalInfo( String additionalInfo )

}
