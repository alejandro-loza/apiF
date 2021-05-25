package mx.finerio.api.services

import mx.finerio.api.dtos.email.EmailFromDto
import mx.finerio.api.dtos.email.EmailSendDto
import mx.finerio.api.dtos.email.EmailTemplateDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

class MagicLinkService {

    @Autowired
    CustomerService customerService

    @Autowired
    CountryService countryService

    @Autowired
    CustomerLinkService customerLinkService

    @Autowired
    EmailRestService emailRestService

    @Autowired
    FinancialInstitutionService financialInstitutionService

    @Value( '${email.api.magicLink.defaultTemplate}' )
    String defaultTemplate


    void sendMagicLink( Long customerId, String countryId ) throws Exception {

        if ( customerId == null ) {
            throw new IllegalArgumentException(
                    'customerEmailService.sendMagicLink.customerId.null' )
        }
        if ( countryId == null ) {
            throw new IllegalArgumentException(
                    'customerEmailService.sendMagicLink.countryId.null' )
        }

        def customer = customerService.findOne( customerId )
        if ( customer == null ) {
            throw new IllegalArgumentException(
                    'customerEmailService.sendMagicLink.customer.null' )
        }

        def country = countryService.findOne( countryId )
        if ( country == null ) {
            throw new IllegalArgumentException(
                    'customerEmailService.sendMagicLink.country.null' )
        }

       //TODO review and add client profile

        def client = customer.client
        def userLink = customerLinkService.findOneByCustomerAndCountry( customer, country )

        if( !userLink ){
            userLink = customerLinkService.createCustomerLink( customer, country )
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


    Map findBanksByCustomerLinkId( String customerLinkId )throws Exception {
        def customerLink = customerLinkService.findOne(customerLinkId)
        def countryCode = customerLink.country?.code
        financialInstitutionService.findAll( [ country: countryCode ] )

    }

}
