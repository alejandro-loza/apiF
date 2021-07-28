package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import static mx.finerio.api.domain.Credential.Status.VALIDATE
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.CustomerLink
import mx.finerio.api.domain.Field
import mx.finerio.api.dtos.CreateCredentialV2Dto
import mx.finerio.api.dtos.CredentialDto
import spock.lang.Specification


class MagicLinkServiceCreateCredentialSpec extends Specification {

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
        def result = service.createCredential( customerLinkId, createCredentialV2Dto )
        then:
        1 * customerLinkService.findOneByLinkId( _ as  String ) >> new CustomerLink(linkId:'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR', customer: new Customer(client: new Client(id:1)))
        1 * securityService.getCurrent() >> new Client(id:1)
        2 * rsaCryptService.decrypt( _ as String ) >> 'dataderypted'
        1 * credentialService.create( _ as CredentialDto ) >> new Credential(id:'someid',username: 'jose',status: VALIDATE,automaticFetching: true, dateCreated: new Date())
        1 * credentialService.getFields( _ as Credential )>> [:]
        result instanceof Map
        where:
        customerLinkId = 'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR'
        createCredentialV2Dto = new CreateCredentialV2Dto(bankId: 1, fields: [new Field(name:'username',value: 'juanito'),new Field(name:'password',value: 'secreto')] )
    }

    def "parameter 'customerLinkId' is null"() {

        when:
        def result = service.createCredential( customerLinkId, createCredentialV2Dto )
        then:
        IllegalArgumentException e = thrown()
        e.message == 'magicLinkService.validateCustomerLink.customerLinkId.null'
        where:
        customerLinkId = null
        createCredentialV2Dto = new CreateCredentialV2Dto(bankId: 1, fields: [new Field(name:'username',value: 'juanito'),new Field(name:'password',value: 'secreto')] )
    }

    def "invalid client "() {

        when:
        def result = service.createCredential( customerLinkId, createCredentialV2Dto )
        then:
        1 * customerLinkService.findOneByLinkId( _ as  String ) >> new CustomerLink(linkId:'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR', customer: new Customer(client: new Client(id:1)))
        1 * securityService.getCurrent() >> new Client(id:2)
        IllegalArgumentException e = thrown()
        e.message == 'magicLinkService.validateClient.customerId.incorrect'
        where:
        customerLinkId = 'dtgtgdvfgfiplkmndsgtgrfrgervTHGEFFDR'
        createCredentialV2Dto = new CreateCredentialV2Dto(bankId: 1, fields: [new Field(name:'username',value: 'juanito'),new Field(name:'password',value: 'secreto')] )
    }

}