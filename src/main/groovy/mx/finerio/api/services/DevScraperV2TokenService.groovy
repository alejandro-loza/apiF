package mx.finerio.api.services

import mx.finerio.api.dtos.ScraperV2TokenDto
import org.springframework.stereotype.Service
import mx.finerio.api.domain.Client
import org.springframework.context.annotation.Profile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import mx.finerio.api.dtos.WidgetEventsDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.domain.Callback

@Service
@Profile('dev')
class DevScraperV2TokenService implements ScraperV2TokenService {

	@Autowired
    CallbackService callbackService

  @Autowired
  CredentialService credentialService

  @Autowired
  ScraperService scraperService

    @Autowired
	WidgetEventsService widgetEventsService

	final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.services.DevScraperV2TokenService' )
	     
  @Override
  void send( String token, String credentialId, String bankCode ) throws Exception {

    def credential = credentialService.findAndValidate( credentialId )
    def data = [
      id: credential.id,
      username: credential.username,
      password: credential.password,
      iv: credential.iv,
      user: [ id: credential.user.id ],
      institution: [ id: 2 ],
      securityCode: credential.securityCode
    ]
    scraperService.requestData( data )

  }
    
	void processOnInteractive( ScraperV2TokenDto scraperV2TokenDto, Client client ) throws Exception  {
	

		validateInteractive( scraperV2TokenDto )		  	
		String credentialId = scraperV2TokenDto.state		   		  						 		
		def dataSend = [ credentialId: credentialId, stage: 'interactive' ]
		def token = scraperV2TokenDto?.data?.value		
		if( token ) {		
			dataSend.put('bankToken', token )
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
