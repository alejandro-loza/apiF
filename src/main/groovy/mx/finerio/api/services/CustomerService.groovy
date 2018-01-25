package mx.finerio.api.services

import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CustomerRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CustomerService {

  @Autowired
  SecurityService securityService

  @Autowired
  CustomerRepository customerRepository

  Customer create( Map params ) throws Exception {

    if ( params == null ) {
      throw new IllegalArgumentException(
          'customerService.create.params.null' )
    }
 
    if ( params == [:] ) {
      throw new IllegalArgumentException(
          'customerService.create.params.empty' )
    }
 
    def instance = new Customer()
    instance.name = params.name
    instance.client = securityService.getCurrent()
    customerRepository.save( instance )
    instance

  }

}
