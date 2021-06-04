package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientConfig
import mx.finerio.api.domain.repository.ClientConfigRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClientConfigService {

    @Autowired
    ClientConfigRepository clientConfigRepository

    List<ClientConfig> findByClientLikeProperty(Client client, String property  )  throws Exception {
        clientConfigRepository.findByDateDeletedIsNullAndClientAndPropertyContains( client, property )
    }



}
