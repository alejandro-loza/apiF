package mx.finerio.api.services

import mx.finerio.api.dtos.ScraperV2TokenDto
import org.springframework.stereotype.Service
import mx.finerio.api.domain.Client
import org.springframework.context.annotation.Profile


@Service
@Profile('dev')
class DevScraperV2TokenService implements ScraperV2TokenService {
	     
	void send( String token, String credentialId, String bankCode ) throws Exception {
	
	}
    
	void processOnInteractive( ScraperV2TokenDto scraperV2TokenDto, Client client ) throws Exception  {
			
	}

  	
}
