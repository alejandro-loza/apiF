package mx.finerio.api.services

import javax.validation.Valid

import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CustomerRepository
import mx.finerio.api.dtos.CustomerDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CustomerService {

  @Autowired
  SecurityService securityService

  @Autowired
  CustomerRepository customerRepository

  Customer create( @Valid CustomerDto customerDto ) throws Exception {

    if ( !customerDto ) {
      throw new BadImplementationException(
          'customerService.create.customerDto.null' )
    }
 
    def client = securityService.getCurrent()

    if ( customerRepository.findByClientAndName( client, customerDto.name ) ) {
      throw new BadRequestException( 'customer.create.name.exists' )
    }
 
    def instance = new Customer()
    instance.name = customerDto.name
    instance.client = client
    customerRepository.save( instance )
    instance

  }

}
