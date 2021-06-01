package mx.finerio.api.services

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import mx.finerio.api.dtos.ScraperV2TokenDto
import mx.finerio.api.domain.Callback

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Client
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.dtos.WidgetEventsDto
import org.springframework.context.annotation.Profile


@Service
@Profile('prod')
class ProdScraperV2TokenService implements ScraperV2TokenService {

	@Autowired
    ScraperV2ClientService scraperV2ClientService

    @Autowired
    CallbackService callbackService

    @Autowired
    CredentialService credentialService

    @Autowired
	WidgetEventsService widgetEventsService

      	
	void send( String token, String credentialId, String bankCode ) throws Exception {

		validateSend( token, credentialId, bankCode )
   
        bankCode = bankCode.toLowerCase()
						
		def data = [ field_name: 'otp',
		             value: token, 
		             content_type: 'text/plain' ]

		def finalData = [
			institution: bankCode,
			state: credentialId,
			data: data] 
								
		scraperV2ClientService.sendInteractive( finalData )		
	}
    
	void processOnInteractive( ScraperV2TokenDto scraperV2TokenDto, Client client ) throws Exception {

		validateInteractive( scraperV2TokenDto )		  	
		String credentialId = scraperV2TokenDto.state
		def credential = credentialService.findAndValidate( credentialId )
		def dataSend = [ customerId: credential.customer.id,
			credentialId: credentialId, stage: 'interactive' ]
		def token = scraperV2TokenDto?.data?.value		
		if( token ) {		
			dataSend.put('bankToken', token )
			dataSend.put('contentType', scraperV2TokenDto?.data?.content_type )
		}

		widgetEventsService.onInteractive( new WidgetEventsDto(
		      credentialId: credentialId, bankToken: token ) )	

		callbackService.sendToClient( client, Callback.Nature.NOTIFY, dataSend )		 	
	}

 
 	private validateInteractive( ScraperV2TokenDto scraperV2TokenDto ) {
		
		if ( scraperV2TokenDto == null ) {
			throw new BadImplementationException(
			 'scraperV2TokenService.validateInteractive.scraperV2TokenDto.null' )
		}
		   		    
		if ( scraperV2TokenDto.state == null ) {
			   throw new BadImplementationException(
				   'scraperV2TokenService.validateInteractive.scraperV2TokenDto.state.null' )
		}
	}
	

	private validateSend( String token, String credentialId, String bankCode ) {
		
		if ( token == null ) {
			throw new BadImplementationException(
			 'scraperV2TokenService.validateSend.token.null' )
		}

		if ( credentialId == null ) {
			throw new BadImplementationException(
			 'scraperV2TokenService.validateSend.credentialId.null' )
		}

		if ( bankCode == null ) {
			throw new BadImplementationException(
			 'scraperV2TokenService.validateSend.bankCode.null' )
		}

	} 	

}
