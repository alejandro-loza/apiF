package mx.finerio.api.services

import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CredentialService {

  @Autowired
  CredentialPersistenceService credentialPersistenceService

  @Autowired
  DevScraperService scraperService

  void requestData( String credentialId ) throws Exception {

    def credential = credentialPersistenceService.findOne( credentialId )

    if ( !credential ) {
      throw new InstanceNotFoundException(
          'credential.requestData.credential.null' )
    }

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
