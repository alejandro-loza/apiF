package mx.finerio.api.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import mx.finerio.api.dtos.CreateCredentialDto
import java.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import mx.finerio.api.exceptions.BadImplementationException
import groovy.json.JsonOutput
import org.springframework.context.annotation.Profile

@Service
@Profile('prod')
class ProdScraperV2Service implements ScraperV2Service {

	@Autowired
    RsaCryptScraperV2Service rsaCryptScraperV2Service

    @Autowired
    CallbackGatewayClientService callbackGatewayClientService

	@Autowired
    ScraperV2ClientService scraperV2ClientService

    @Value('${gateway.source}')
	String source

	@Value('${scraperv2.rangeDates.monthsAgo}') 
    int monthsAgo
    
    @Override
	void createCredential( CreateCredentialDto createCredentialDto ) throws Exception {

		validateCreateCredetial( createCredentialDto )
             			
		def jsonMap = [:]
		jsonMap.username = createCredentialDto.username
		jsonMap.password = createCredentialDto.password
		
		def state = createCredentialDto.credentialId		
		def jsonString = JsonOutput.toJson( jsonMap )				
		def jsonBase64 = jsonString.getBytes( 'UTF-8' ).encodeBase64().toString()		
		def jsonEncrypted = rsaCryptScraperV2Service.encrypt( jsonBase64 )
		
		LocalDate now = LocalDate.now()		
		String endDate = now.toString()		
	    String startDate = now.minusMonths( monthsAgo ).toString()
							    
		def finalData = [ institution: createCredentialDto.bankCode.toLowerCase(),
			data: jsonEncrypted,
			state: state,
			start_date: startDate,
			end_date: endDate ]

		callbackGatewayClientService
			.registerCredential( [ credentialId: state ,source: source ] )
		         	
		 scraperV2ClientService.createCredential( finalData )									
		
	}

	private void validateCreateCredetial( CreateCredentialDto createCredentialDto ){

		if ( createCredentialDto == null ) {
			 throw new BadImplementationException(
			  'scraperV2Service.validateCreateCredetial.createCredentialDto.null' )
		    }

	}


	@Override
	void createCredentialLegacyPayload( Map data ) throws Exception {	
	
        def credentialId = data.id    			
		def finalData = [ data: [ data ] ]

		callbackGatewayClientService
			.registerCredential( [ credentialId: credentialId ,source: source ] )
		         	
		 scraperV2ClientService.createCredentialLegacy( finalData )									
		
	}

}
