package mx.finerio.api.services

import org.springframework.stereotype.Service
import mx.finerio.api.dtos.CreateCredentialDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import mx.finerio.api.domain.repository.*
import org.springframework.beans.factory.annotation.Autowired

@Service
@Profile('dev')
class DevScraperV2Service implements ScraperV2Service {

	@Autowired
  CredentialRepository credentialRepository

  @Autowired
  ScraperService scraperService
   
  @Override
	void createCredential( CreateCredentialDto createCredentialDto ) throws Exception {
	
		def credential = credentialRepository
			.findOne( createCredentialDto.credentialId )

		def data = [
      		id: credential.id,
      		username: credential.username,
      		password: credential.password,
      		iv: credential.iv,
      		user: [ id: credential.user.id ],
      		institution: [ id: credential.institution.id ],
      		securityCode: credential.securityCode
    	]

    	scraperService.requestData( data )	
	}



}
