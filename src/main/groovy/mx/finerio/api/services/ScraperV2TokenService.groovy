package mx.finerio.api.services

import mx.finerio.api.dtos.ScraperV2TokenDto
import mx.finerio.api.domain.Client


interface ScraperV2TokenService {
     
	void send( String token, String credentialId, String bankCode ) throws Exception     
	void processOnInteractive( ScraperV2TokenDto scraperV2TokenDto, Client client ) throws Exception 

}
