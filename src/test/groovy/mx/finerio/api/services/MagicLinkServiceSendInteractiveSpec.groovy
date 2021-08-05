package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.dtos.CredentialInteractiveDto
import mx.finerio.api.dtos.CredentialInteractiveMagicLinkDto

import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.CustomerLink
import spock.lang.Specification


class MagicLinkServiceSendInteractiveSpec extends Specification {

    def service = new MagicLinkService()
    def clientConfigService = Mock( ClientConfigService )
    def credentialService = Mock( CredentialService )
    def customerLinkService = Mock( CustomerLinkService )
    def securityService = Mock( SecurityService )
    def rsaCryptService = Mock( RsaCryptService )

    def setup() {
        service.clientConfigService = clientConfigService
        service.credentialService = credentialService
        service.customerLinkService = customerLinkService
        service.securityService = securityService
        service.rsaCryptService = rsaCryptService
    }

    def "invoking method successfully"() {

        when:
        service.sendInteractive( customerLinkId, credentialId, credentialInteractiveMagicLinkDto )
        then:
        1 * customerLinkService.findOneByLinkId( _ as  String ) >> new CustomerLink(linkId:'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR', customer: new Customer(client: new Client(id:1)))
        1 * securityService.getCurrent() >> new Client(id:1)
        1 * credentialService.processInteractive( _ as String, _ as CredentialInteractiveDto, _ as Client)
        where:
        customerLinkId = 'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR'
        credentialId = 'dsfs-erer-ere-sdfsd-sdf'
        credentialInteractiveMagicLinkDto = new CredentialInteractiveMagicLinkDto( otp: 'token' )
    }

    def "parameter 'customerLinkId' is null"() {

        when:
        service.sendInteractive( customerLinkId, credentialId, credentialInteractiveMagicLinkDto )
        then:
        IllegalArgumentException e = thrown()
        e.message == 'magicLinkService.validateCustomerLink.customerLinkId.null'
        where:
        customerLinkId = null
        credentialId = 'dsfs-erer-ere-sdfsd-sdf'
        credentialInteractiveMagicLinkDto = new CredentialInteractiveMagicLinkDto( otp: 'token' )
    }

    def "invalid client "() {

        when:
        service.sendInteractive( customerLinkId, credentialId, credentialInteractiveMagicLinkDto )
        then:
        1 * customerLinkService.findOneByLinkId( _ as  String ) >> new CustomerLink(linkId:'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR', customer: new Customer(client: new Client(id:1)))
        1 * securityService.getCurrent() >> new Client(id:2)
        IllegalArgumentException e = thrown()
        e.message == 'magicLinkService.validateClient.customerId.incorrect'
        where:
        customerLinkId = 'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR'
        credentialId = 'dsfs-erer-ere-sdfsd-sdf'
        credentialInteractiveMagicLinkDto = new CredentialInteractiveMagicLinkDto( otp: 'token' )
    }

}
