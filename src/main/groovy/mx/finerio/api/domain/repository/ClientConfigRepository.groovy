package mx.finerio.api.domain.repository

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import  mx.finerio.api.domain.ClientConfig.Property

interface ClientConfigRepository extends JpaRepository<ClientConfig, Long>,
        JpaSpecificationExecutor{

     List<ClientConfig> findByDateDeletedIsNullAndClientAndPropertyLike( Client client, Property property )

}
