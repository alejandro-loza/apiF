package mx.finerio.api.services

import mx.finerio.api.dtos.CreateCredentialDto

interface ScraperV2Service {
	void createCredential( CreateCredentialDto createCredentialDto ) throws Exception 
	void createCredentialLegacyPayload( Map data ) throws Exception
}
	