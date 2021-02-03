package mx.finerio.api.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import mx.finerio.api.dtos.CreateCredentialDto
import java.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired

@Service
class ScraperV2TokenService {

	@Autowired
    ScraperV2ClientService scraperV2ClientService
      	
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

}
