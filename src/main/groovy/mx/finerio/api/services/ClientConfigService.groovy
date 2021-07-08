package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientConfig
import mx.finerio.api.domain.repository.ClientConfigRepository
import mx.finerio.api.exceptions.BadImplementationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import static mx.finerio.api.domain.ClientConfig.Property.SATWS_APIKEY

@Service
class ClientConfigService {

    @Autowired
    ClientConfigRepository clientConfigRepository

    @Autowired
    SecurityService securityService

    List<ClientConfig> findByClientLikeProperty(Client client, String property  )  throws Exception {
        clientConfigRepository.findByDateDeletedIsNullAndClientAndPropertyContains( client, property )
    }

    String getCurrentApiKey() throws Exception {

        Client client = securityService.getCurrent()
        String property = SATWS_APIKEY.name()
        ClientConfig clientConfig = clientConfigRepository
                .findOneByDateDeletedIsNullAndClientAndProperty( client, property )

        if(!clientConfig){
            throw new BadImplementationException("clientConfigService.getCurrentApiKey.apiKey.unset")
        }

        def res = clientConfig.property
        res
    }





}
