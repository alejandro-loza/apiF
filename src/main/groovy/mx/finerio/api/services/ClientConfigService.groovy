package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientConfig
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.ClientConfigRepository
import mx.finerio.api.exceptions.BadImplementationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import static mx.finerio.api.domain.ClientConfig.Property.SATWS_API_KEY
import static mx.finerio.api.domain.ClientConfig.Property.INSTITUTIONS_GRANTED

@Service
class ClientConfigService {

    @Value( '${magiclink.client.username}' )
    String magicLinkUsername

    @Autowired
    ClientConfigRepository clientConfigRepository

    @Autowired
    SecurityService securityService

    @Autowired
    CustomerService customerService

    List<ClientConfig> findByClientLikeProperty(Client client, String property  )  throws Exception {
        clientConfigRepository.findByDateDeletedIsNullAndClientAndPropertyContains( client, property )
    }

    String getCurrentApiKey( Long customerId = null ) throws Exception {

        Client client = securityService.getCurrent()
        String customerName = null
        if( customerId ){
            Customer customer = customerService.findOne( customerId, client )
            customerName = customer.name
        }

        def propertyName = getPropertyName( client, customerName  )

        ClientConfig clientConfig = clientConfigRepository
                .findOneByDateDeletedIsNullAndClientAndProperty( client, propertyName )

        if( !clientConfig ){
            throw new BadImplementationException("clientConfigService.getCurrentApiKey.apiKey.unset")
        }

        def res = clientConfig.value
        res
    }

    private String getPropertyName( Client client, String name =null ) throws Exception {

        if( client.username.equals( magicLinkUsername )  )  {
            return "${SATWS_API_KEY.name()}*$name"
        }else{
            return SATWS_API_KEY.name()
        }

    }

    Boolean isInstitutionGranted ( Client client, String institutionCode ) throws Exception {

        String property = INSTITUTIONS_GRANTED.name()
        ClientConfig clientConfig = clientConfigRepository
                .findOneByDateDeletedIsNullAndClientAndProperty( client, property )

        if(!clientConfig){
            return false
        }
        String[] arrGrants = clientConfig.property.split(',')
        arrGrants.contains(institutionCode) ? true : false

    }





}
