package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.CustomerLink

import spock.lang.Specification

 class MagicLinkServiceFindBanksByCustomerLinkSpec extends Specification {

     def service = new MagicLinkService()

     def clientConfigService = Mock( ClientConfigService )
     def financialInstitutionService = Mock( FinancialInstitutionService )
     def customerLinkService = Mock( CustomerLinkService )
     def securityService = Mock( SecurityService )

    def setup() {
        service.clientConfigService = clientConfigService
        service.financialInstitutionService = financialInstitutionService
        service.customerLinkService = customerLinkService
        service.securityService = securityService
    }

    def "invoking method successfully"() {

        when:
        def result = service.findBanksByCustomerLinkId( customerLinkId )
        then:
        1 * customerLinkService.findOneByLinkId( _ as  String ) >> new CustomerLink(linkId:'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR', customer: new Customer(client: new Client(id:1)))
        1 * securityService.getCurrent() >> new Client(id:1)
        2 * clientConfigService.findByClientLikeProperty( _ as Client, _ as String ) >> [[value:'hey']]
        1 * financialInstitutionService.findAllByCountriesAndTypes( _ as Map ) >> [value:'hey']
        result instanceof Map
        where:
        customerLinkId = 'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR'

    }

    def "parameter 'customerLinkId' is null"() {

        when:
        service.findBanksByCustomerLinkId( customerLinkId )
        then:
        IllegalArgumentException e = thrown()
        e.message == 'magicLinkService.validateCustomerLink.customerLinkId.null'
        where:
        customerLinkId = null

    }

     def "invalid client "() {

         when:
         def result = service.findBanksByCustomerLinkId( customerLinkId )
         then:
         1 * customerLinkService.findOneByLinkId( _ as  String ) >> new CustomerLink(linkId:'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR', customer: new Customer(client: new Client(id:1)))
         1 * securityService.getCurrent() >> new Client(id:2)
         IllegalArgumentException e = thrown()
         e.message == 'magicLinkService.validateClient.customerId.incorrect'
         where:
         customerLinkId = 'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR'

     }

}

