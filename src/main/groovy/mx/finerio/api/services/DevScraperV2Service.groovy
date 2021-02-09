package mx.finerio.api.services

import org.springframework.stereotype.Service
import mx.finerio.api.dtos.CreateCredentialDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile

@Service
@Profile('dev')
class DevScraperV2Service implements ScraperV2Service {
   
    @Override
	void createCredential( CreateCredentialDto createCredentialDto ) throws Exception {

		
		
	}



}
