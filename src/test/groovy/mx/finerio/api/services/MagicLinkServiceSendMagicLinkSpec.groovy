package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.dtos.CredentialInteractiveDto
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.CustomerLink
import mx.finerio.api.dtos.email.EmailSendDto
import spock.lang.Specification


class MagicLinkServiceSendMagicLinkSpec extends Specification {

    def service = new MagicLinkService()
    def clientConfigService = Mock( ClientConfigService )
    def credentialService = Mock( CredentialService )
    def customerLinkService = Mock( CustomerLinkService )
    def securityService = Mock( SecurityService )
    def customerService = Mock( CustomerService )
    def emailRestService = Mock( EmailRestService )

    def setup() {
        service.clientConfigService = clientConfigService
        service.credentialService = credentialService
        service.customerLinkService = customerLinkService
        service.securityService = securityService
        service.customerService = customerService
        service.emailRestService = emailRestService
    }

    def "invoking method successfully"() {

        when:
        service.sendMagicLink( customerId )
        then:
        1 * customerService.findOne( _ as Long  ) >> new Customer( id: 2, client: new Client(id:1))
        1 * securityService.getCurrent() >> new Client(id:1)
        1 * clientConfigService.findByClientLikeProperty( _ as Client, _ as String )
        1 * customerLinkService.findOneByCustomer( _ as  Customer) >> new CustomerLink(linkId:'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR', customer: new Customer(client: new Client(id:1)))
        1* emailRestService.send(_ as EmailSendDto )
        where:
        customerId = 2
    }

     def "parameter 'customerId' is null"() {

        when:
        service.sendMagicLink( customerId )
        then:
        IllegalArgumentException e = thrown()
        e.message == 'magicLinkService.sendMagicLink.customerId.null'
        where:
        customerId = null
    }

    def "customer not found "() {

        when:
        service.sendMagicLink( customerId )
        then:
        1 * customerService.findOne( _ as Long  ) >> null
        IllegalArgumentException e = thrown()
        e.message == 'magicLinkService.sendMagicLink.customer.null'
        where:
        customerId = 2
    }

}

