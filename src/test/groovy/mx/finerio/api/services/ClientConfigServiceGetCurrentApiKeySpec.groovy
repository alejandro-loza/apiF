package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientConfig
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import spock.lang.Specification
import mx.finerio.api.domain.repository.ClientConfigRepository

class ClientConfigServiceGetCurrentApiKeySpec extends Specification {

    def service = new ClientConfigService()

    def securityService = Mock( SecurityService )
    def clientConfigRepository = Mock( ClientConfigRepository )

    def setup() {
        service.securityService = securityService
        service.clientConfigRepository = clientConfigRepository
    }

    def "everything is OK"() {

        when:
        def result = service.getCurrentApiKey()
        then:
        1 * securityService.getCurrent() >> new Client()
        1 * clientConfigRepository.findOneByDateDeletedIsNullAndClientAndProperty(
                _ as Client, _ as String) >> new ClientConfig(property: 'gdvdty4erfrdg')

        result instanceof String
        result == 'gdvdty4erfrdg'
    }

    def "should throw an exception"() {

        when:
        service.getCurrentApiKey()
        then:
        1 * securityService.getCurrent() >> new Client()
        1 * clientConfigRepository.findOneByDateDeletedIsNullAndClientAndProperty(
                _ as Client, _ as String) >> null


        BadImplementationException e = thrown()
        e.message == 'clientConfigService.getCurrentApiKey.apiKey.unset'


    }



}

