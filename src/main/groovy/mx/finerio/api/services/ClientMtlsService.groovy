package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientMtls
import mx.finerio.api.domain.repository.ClientMtlsRepository
import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClientMtlsService {

  @Autowired
  ClientMtlsRepository clientMtlsRepository

  ClientMtls findByClient( Client client ) throws Exception {

    if ( client ==  null ) {
      throw new BadImplementationException(
          'clientMltsService.findByClient.client.null' )
    }

    return clientMtlsRepository.findByClient( client )

  }

}

