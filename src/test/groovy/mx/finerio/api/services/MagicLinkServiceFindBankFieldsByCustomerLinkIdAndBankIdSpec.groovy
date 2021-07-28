package mx.finerio.api.services

import mx.finerio.api.domain.BankField
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.CustomerLink

import spock.lang.Specification

class MagicLinkServiceFindBankFieldsByCustomerLinkIdAndBankIdSpec extends Specification {

    def service = new MagicLinkService()

    def clientConfigService = Mock( ClientConfigService )
    def bankFieldService = Mock( BankFieldService )
    def customerLinkService = Mock( CustomerLinkService )
    def securityService = Mock( SecurityService )

    def setup() {
        service.clientConfigService = clientConfigService
        service.bankFieldService = bankFieldService
        service.customerLinkService = customerLinkService
        service.securityService = securityService
    }

    def "invoking method successfully"() {

        when:
        def result = service.findBankFieldsByCustomerLinkIdAndBankId( customerLinkId, bankId )
        then:
        1 * customerLinkService.findOneByLinkId( _ as  String ) >> new CustomerLink(linkId:'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR', customer: new Customer(client: new Client(id:1)))
        1 * securityService.getCurrent() >> new Client(id:1)
        1 * bankFieldService.findAllByFinancialInstitution( _ as Long ) >> [new BankField( name: 'Banco', friendlyName: 'Banco', position: 1, type: 'customer', required: true )]
        1 * bankFieldService.getFields( _ as BankField )>> [:]
        result instanceof List
        where:
        customerLinkId = 'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR'
        bankId = 1




    }

    def "parameter 'customerLinkId' is null"() {

        when:
        service.findBankFieldsByCustomerLinkIdAndBankId( customerLinkId, bankId )
        then:
        IllegalArgumentException e = thrown()
        e.message == 'magicLinkService.validateCustomerLink.customerLinkId.null'
        where:
        customerLinkId = null
        bankId = 1

    }

    def "invalid client "() {

        when:
        service.findBankFieldsByCustomerLinkIdAndBankId( customerLinkId, bankId )
        then:
        1 * customerLinkService.findOneByLinkId( _ as  String ) >> new CustomerLink(linkId:'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR', customer: new Customer(client: new Client(id:1)))
        1 * securityService.getCurrent() >> new Client(id:2)
        IllegalArgumentException e = thrown()
        e.message == 'magicLinkService.validateClient.customerId.incorrect'
        where:
        customerLinkId = 'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR'
        bankId = 1

    }

}

