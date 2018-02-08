package mx.finerio.api.services

import javax.validation.Valid

import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.CustomerSpecs
import mx.finerio.api.domain.repository.CustomerRepository
import mx.finerio.api.dtos.CustomerDto
import mx.finerio.api.dtos.CustomerListDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class CustomerService {

  private static final Integer MAX_RESULTS = 100

  @Autowired
  ListService listService

  @Autowired
  SecurityService securityService

  @Autowired
  CustomerRepository customerRepository

  Customer create( @Valid CustomerDto dto ) throws Exception {

    if ( !dto ) {
      throw new BadImplementationException(
          'customerService.create.dto.null' )
    }
 
    def client = securityService.getCurrent()

    if ( customerRepository.findByClientAndName( client, dto.name ) ) {
      throw new BadRequestException( 'customer.create.name.exists' )
    }
 
    def instance = new Customer()
    instance.name = dto.name
    instance.client = client
    instance.dateCreated = new Date()
    customerRepository.save( instance )
    instance

  }

  Map findAll( Map params ) throws Exception {

    if ( params == null ) {
      throw new BadImplementationException(
          'customerService.findAll.params.null' )
    }
 
    def dto = getFindAllDto( params )
    def spec = CustomerSpecs.findAll( dto )
    listService.findAll( dto, customerRepository, spec )

  }

  Customer findOne( Long id ) throws Exception {

    if ( id == null ) {
      throw new BadImplementationException(
          'customerService.findOne.id.null' )
    }
 
    def client = securityService.getCurrent()
    def instance = customerRepository.findOne( id )

    if ( !instance || instance.client.id != client.id ) {
      throw new InstanceNotFoundException( 'customer.not.found' )
    }
 
    instance

  }

  CustomerListDto getFindAllDto( Map params ) throws Exception {

    def dto = new CustomerListDto()
    listService.validateFindAllDto( dto, params )

    if ( params.cursor ) {
      try {
        findOne( params.cursor as Long )
        dto.cursor = params.cursor as Long
      } catch ( NumberFormatException e ) {
        throw new BadRequestException( 'cursor.invalid' )
      }
    }

    dto.client = securityService.getCurrent()
    dto

  }

  Map getFields( Customer customer ) throws Exception {

    if ( !customer ) {
      throw new BadImplementationException(
          'customerService.getFields.customer.null' )
    }
 
    [ id: customer.id, name: customer.name, dateCreated: customer.dateCreated ]

  }

}
