package mx.finerio.api.services


import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import mx.finerio.api.dtos.ScraperV2TokenDto
import mx.finerio.api.domain.Callback

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Client



@Service
class ScraperV2TokenService {

	@Autowired
    ScraperV2ClientService scraperV2ClientService

    @Autowired
    CallbackService callbackService

     @Autowired
  CredentialService credentialService
      	
	void send( String token, String credentialId, String bankCode ) {
						
		def data = [ field_name: 'otp',
		             value: token, 
		             content_type: 'text/plain' ]

		def finalData = [
			institution: bank.code,
			state: credentialId,
			data: data] 
								
		scraperV2ClientService.sendInteractive( finalData )		
	}
    //TODO check if it is necesary to add widget functionality
	void processOnInteractive( ScraperV2TokenDto scraperV2TokenDto, Client client ) {

		validateInteractive( scraperV2TokenDto )		  	
		String credentialId = scraperV2TokenDto.state		   		  						 		
		def dataSend = [ credentialId: credentialId, stage: 'interactive' ]
		def token = scraperV2TokenDto?.data?.value		
		if( token ) {		
			dataSend.put('bankToken', token )
		}		  		  
		callbackService.sendToClient( client, Callback.Nature.NOTIFY, dataSend )		 
	
	}

 
 	private validateInteractive( ScraperV2TokenDto scraperV2TokenDto ) {
		
		if ( scraperV2TokenDto == null ) {
			throw new IllegalArgumentException(
			 'scraperV2TokenService.validateInteractive.scraperV2TokenDto.null' )
		}
		   		    
		if ( scraperV2TokenDto.state == null ) {
			   throw new IllegalArgumentException(
				   'scraperV2TokenService.validateInteractive.scraperV2TokenDto.state.null' )
		}
	}
	 	

}
