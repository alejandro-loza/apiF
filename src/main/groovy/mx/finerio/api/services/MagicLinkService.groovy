package mx.finerio.api.services

import mx.finerio.api.domain.ClientConfig
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.dtos.email.EmailFromDto
import mx.finerio.api.dtos.email.EmailSendDto
import mx.finerio.api.dtos.email.EmailTemplateDto
import mx.finerio.api.exceptions.BadImplementationException
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

    Map findBanksByCustomerLinkId( String customerLinkId ) throws Exception {

        def customerLink = customerLinkService.findOneByLinkId( customerLinkId )
        def client = customerLink?.customer?.client

        def clientConFigCountries = clientConfigService.findClientsConfigByClientLikeProperty( client, COUNTRY_CODE.name() )
        def clientConFigTypes = clientConfigService.findClientsConfigByClientLikeProperty( client, INSTITUTION_TYPE.name() )


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

}
