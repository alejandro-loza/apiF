package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientConfig
import mx.finerio.api.domain.Customer
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import spock.lang.Specification
import mx.finerio.api.domain.repository.ClientConfigRepository

class ClientConfigServiceGetCurrentApiKeySpec extends Specification {

    def service = new ClientConfigService()

    def securityService = Mock( SecurityService )
    def clientConfigRepository = Mock( ClientConfigRepository )
    def customerService = Mock( CustomerService )



    def setup() {
        service.securityService = securityService
        service.clientConfigRepository = clientConfigRepository
        service.customerService = customerService
    }

    def "everything is OK"() {

        when:
        def result = service.getCurrentApiKey(1L)
        then:
        1 * securityService.getCurrent() >> new Client()
        1 * customerService.findOne( _ as Long, _ as Client) >> new Customer(name: 'name')
        1 * clientConfigRepository.findOneByDateDeletedIsNullAndClientAndProperty(
                _ as Client, _ as String) >> new ClientConfig(value: 'gdvdty4erfrdg')

        result instanceof String
        result == 'gdvdty4erfrdg'
    }

    def "should throw an exception"() {

        when:
        service.getCurrentApiKey(1l)
        then:
        1 * securityService.getCurrent() >> new Client()
        1 * customerService.findOne( _ as Long, _ as Client) >> new Customer(name: 'name')
        1 * clientConfigRepository.findOneByDateDeletedIsNullAndClientAndProperty(
                _ as Client, _ as String) >> null


        BadImplementationException e = thrown()
        e.message == 'clientConfigService.getCurrentApiKey.apiKey.unset'


    }

}

