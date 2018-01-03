package mx.finerio.api.services

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.repository.CredentialRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CredentialPersistenceService {

  @Autowired
  CredentialRepository credentialRepository

  Credential findOne( String id ) throws Exception {

    if ( !id ) {
      return null
    }

    credentialRepository.findOne( id )
  }

}
