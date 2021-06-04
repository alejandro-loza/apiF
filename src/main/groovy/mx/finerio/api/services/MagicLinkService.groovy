package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.dtos.email.EmailFromDto
import mx.finerio.api.dtos.email.EmailSendDto
import mx.finerio.api.dtos.email.EmailTemplateDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import static mx.finerio.api.domain.ClientConfig.Property.COUNTRY_CODE
import static mx.finerio.api.domain.ClientConfig.Property.INSTITUTION_TYPE


@Service
class MagicLinkService {

    @Autowired
    CustomerService customerService

    @Autowired
    ClientConfigService clientConfigService

    @Autowired
    CustomerLinkService customerLinkService

    @Autowired
    EmailRestService emailRestService

    @Autowired
    FinancialInstitutionService financialInstitutionService

    @Autowired
    BankFieldService bankFieldService

    @Autowired
    SecurityService securityService

    @Value( '${email.api.magicLink.defaultTemplate}' )
    String defaultTemplate


    void sendMagicLink( Long customerId ) throws Exception {

        if ( customerId == null ) {
            throw new IllegalArgumentException(
                    'magicLinkService.sendMagicLink.customerId.null' )
        }

        def customer = customerService.findOne( customerId )
        if ( customer == null ) {
            throw new IllegalArgumentException(
                    'magicLinkService.sendMagicLink.customer.null' )
        }


       //TODO review and add client profile

        def client = customer.client
        verifyAgainstCurrentClient( client )

        def userLink = customerLinkService.findOneByCustomer( customer )

        if( !userLink ){
            userLink = customerLinkService.createCustomerLink( customer )
        }

        def dto = new EmailSendDto(
                from: new EmailFromDto(
                        email: client.email,
                        name: client.company,
                ),
                to:  [ customer.name ],
                template: new EmailTemplateDto(
                        name: defaultTemplate,
                        params: [
                                'userName': customer.name,
                                'clientName':  client.company,
                                'linkId': userLink.linkId
                        ]
                )
        )
        emailRestService.send( dto )
    }

    private void verifyAgainstCurrentClient( Client client ) throws Exception {

        def signedClient = (Client) securityService.getCurrent()

        if( client.id != signedClient.id ){
            throw new IllegalArgumentException(
                    'magicLinkService.verifyAgainstCurrentClient.customerId.incorrect' )
        }

    }

    Map findBanksByCustomerLinkId( String customerLinkId ) throws Exception {

        def customerLink = customerLinkService.findOneByLinkId( customerLinkId )
        def client = customerLink?.customer?.client
        verifyAgainstCurrentClient( client )

        def clientConFigCountries = clientConfigService.findByClientLikeProperty( client, COUNTRY_CODE.name() )
        def clientConFigTypes = clientConfigService.findByClientLikeProperty( client, INSTITUTION_TYPE.name() )


        def dataQuery=[:]
        if( !clientConFigCountries?.isEmpty() ) {
            def countries = clientConFigCountries.collect { it.value }
            dataQuery.countries = countries
        }else{
            dataQuery.countries = [ FinancialInstitutionService.defaultCountry ]
        }

        if( !clientConFigTypes?.isEmpty() ) {
            def types = clientConFigTypes.collect { it.value }
            dataQuery.types = types
        }else{
            dataQuery.types = [ FinancialInstitutionService.defaultInstitutionType.name() ]
        }

        financialInstitutionService.findAllByCountriesAndTypes ( dataQuery )
    }

    List findBankFieldsByCustomerLinkIdAndBankId( String customerLinkId, Long bankId ) throws Exception {

        def customerLink = customerLinkService.findOneByLinkId( customerLinkId )
        def client = customerLink?.customer?.client
        verifyAgainstCurrentClient( client )

        def response = bankFieldService.findAllByFinancialInstitution( bankId )
                .collect { bankFieldService.getFields( it ) }
                .sort { it.position }

        response
    }

}
