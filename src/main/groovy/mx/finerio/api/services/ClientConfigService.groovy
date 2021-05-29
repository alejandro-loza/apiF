package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientConfig
import mx.finerio.api.domain.repository.CategoryRepository
import mx.finerio.api.domain.repository.ClientConfigRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.domain.ClientConfig.Property

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClientConfigService {

    @Autowired
    ClientConfigRepository clientConfigRepository

    List<ClientConfig> findClientsConfigByClientLikeProperty( Client client, Property property  )  throws Exception {

        clientConfigRepository.findByDateDeletedIsNullAndClientAndPropertyLike( client, property )

    }



}
