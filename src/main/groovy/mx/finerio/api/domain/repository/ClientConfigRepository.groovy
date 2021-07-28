package mx.finerio.api.domain.repository

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ClientConfigRepository extends JpaRepository<ClientConfig, Long>,
        JpaSpecificationExecutor{

     List<ClientConfig> findByDateDeletedIsNullAndClientAndPropertyContains( Client client, String property )
     ClientConfig findOneByDateDeletedIsNullAndClientAndProperty( Client client, String property )

}
