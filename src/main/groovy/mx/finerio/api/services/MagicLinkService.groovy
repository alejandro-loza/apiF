package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.CustomerLink
import mx.finerio.api.dtos.CreateCredentialV2Dto
import mx.finerio.api.dtos.CredentialDto
import mx.finerio.api.dtos.CredentialFieldDto
import mx.finerio.api.dtos.CredentialInteractiveDto
import mx.finerio.api.dtos.CredentialInteractiveMagicLinkDto
import mx.finerio.api.dtos.email.EmailFromDto
import mx.finerio.api.dtos.email.EmailSendDto
import mx.finerio.api.dtos.email.EmailTemplateDto
import mx.finerio.api.exceptions.BadRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.crypto.BadPaddingException
import static mx.finerio.api.domain.ClientConfig.Property.COUNTRY_CODE
import static mx.finerio.api.domain.ClientConfig.Property.INSTITUTION_TYPE
import static mx.finerio.api.domain.ClientConfig.Property.MAGIC_LINK_EMAIL_TEMPLATE


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

    @Autowired
    CredentialService credentialService

    @Autowired
    RsaCryptService rsaCryptService

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


        def client = validateClient( customer?.client )
        def customerEmailTemplate
        def customerEmailTemplates = clientConfigService
                .findByClientLikeProperty( client, MAGIC_LINK_EMAIL_TEMPLATE.name() )
        if( customerEmailTemplates && !customerEmailTemplates.isEmpty() ){
            customerEmailTemplate = customerEmailTemplates[0]
        }

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
                        name: customerEmailTemplate?:defaultTemplate,
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

        def client = validateClient( customerLinkId )
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
        validateClient( customerLinkId )
        def response = bankFieldService.findAllByFinancialInstitution( bankId )
                .collect { bankFieldService.getFields( it ) }
                .sort { it.position }

        response
    }

    Map createCredential( String customerLinkId, CreateCredentialV2Dto createCredentialV2Dto ) throws Exception {

        def customerLink =validateCustomerLink( customerLinkId )
        def customer = customerLink?.customer
        validateClient( customer?.client )
        def fields = getFieldsMap( createCredentialV2Dto.fields )
        def credentialDto = new CredentialDto(
                username:fields.username,
                password:fields.password,
                bankId:createCredentialV2Dto.bankId,
                customerId:customer.id )
        def instance = credentialService.create( credentialDto )
        credentialService.getFields( instance )
    }

     void sendInteractive(String customerLinkId, String credentialId,
                          CredentialInteractiveMagicLinkDto credentialInteractiveMagicLinkDto  ) throws Exception {
         def client = validateClient(customerLinkId)
         def credentialInteractiveDto =
                 new CredentialInteractiveDto(
                         token: credentialInteractiveMagicLinkDto.otp)
         credentialService.processInteractive(
                 credentialId, credentialInteractiveDto, client )
     }

    private Map getFieldsMap( List<CredentialFieldDto> fields )
            throws Exception {

        def namesMap = [:]
        def fieldsMap = [:]

        for ( field in fields ) {

            if ( namesMap[ field.name ] == null ) {
                namesMap[ field.name ] = 0
            }
            namesMap[ field.name ]++
            fieldsMap[ field.name ] = decrypt(field.value)
        }

        if ( namesMap[ 'username' ] == 1 && namesMap[ 'password' ] == 1 &&
                namesMap[ 'securityCode' ] <= 1 ) {
            return fieldsMap
        }
        throw new BadRequestException( 'credential.fields.invalid' )
    }

    String decrypt(String value){
        def result
        try {
            result = rsaCryptService.decrypt( value )
        } catch ( BadPaddingException e ) {
            throw new BadRequestException( 'cryptedText.invalid' )
        }catch ( BadRequestException e ) {
            throw new BadRequestException( 'rsaCryptService.decrypt.wrongKey' )
        }
        result
    }

    private Client validateClient( String customerLinkId ) throws Exception {
        def customerLink= validateCustomerLink( customerLinkId )
        Client client = customerLink?.customer?.client
        validateClient(client)

    }

    private Client validateClient(Client client ) throws Exception {

        def signedClient = (Client) securityService.getCurrent()
        if( client.id != signedClient.id ){
            throw new IllegalArgumentException(
                    'magicLinkService.validateClient.customerId.incorrect' )
        }
        client
    }

    private CustomerLink validateCustomerLink( String customerLinkId ){

        if( !customerLinkId ){
            throw new IllegalArgumentException(
                    'magicLinkService.validateCustomerLink.customerLinkId.null' )
        }

        def customerLink = customerLinkService.findOneByLinkId( customerLinkId )
        if( !customerLink ){
            throw new IllegalArgumentException(
                    'magicLinkService.validateCustomerLink.customerLink.notFound' )
        }
        customerLink
    }

}
