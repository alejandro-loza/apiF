package mx.finerio.api.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerDeleteService {

  @Autowired
  CredentialService credentialService

  @Autowired
  CustomerService customerService

  @Transactional
  void delete( Long customerId ) throws Exception {

    def params = [ customerId: customerId ]
    def credentials = credentialService.findAll( params ).data

    for ( credential in credentials ) {
      credentialService.delete( credential.id )
    }

    customerService.delete( customerId )

  }

}
