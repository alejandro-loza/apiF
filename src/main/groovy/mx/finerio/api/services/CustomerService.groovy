package mx.finerio.api.services

import javax.validation.Valid

import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.Client
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
import mx.finerio.api.services.AdminService.EntityType

@Service
class CustomerService {

  private static final Integer MAX_RESULTS = 100

  @Autowired
  ListService listService

  @Autowired
  SecurityService securityService

  @Autowired
  CustomerRepository customerRepository

  @Autowired
  AdminService adminService

  Customer create( @Valid CustomerDto dto, Client client = null ) throws Exception {

    if ( !dto ) {
      throw new BadImplementationException(
          'customerService.create.dto.null' )
    }
    if( !client ){
      client = securityService.getCurrent()
    }
    if ( customerRepository
        .findFirstByClientAndNameAndDateDeletedIsNull(
        client, dto.name ) != null ) {
      throw new BadRequestException( 'customer.create.name.exists' )
    }
 
    def instance = new Customer()
    instance.name = dto.name
    instance.client = client
    instance.dateCreated = new Date()
    customerRepository.save( instance )
    adminService.sendDataToAdmin( EntityType.CUSTOMER, instance )
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

  Customer findOne( Long id, Client client = null ) throws Exception {

    if ( id == null ) {
      throw new BadImplementationException(
          'customerService.findOne.id.null' )
    }
    
    if( !client ){
       client = securityService.getCurrent()
    }

    def instance = customerRepository.findOne( id )

    if ( !instance || instance.client.id != client.id ||
        instance.dateDeleted != null ) {
      throw new InstanceNotFoundException( 'customer.not.found' )
    }
 
    instance

  }

  Customer findByName( Client client, String name ) throws Exception {

    if ( client == null ) {
      throw new BadImplementationException(
          'customerService.findByName.client.null' )
    }

    if ( name == null ) {
      throw new BadImplementationException(
          'customerService.findByName.name.null' )
    }

    return customerRepository
        .findFirstByClientAndNameAndDateDeletedIsNull(
        client, name )

  }

  Customer update( Long id, @Valid CustomerDto dto ) throws Exception {

    if ( id == null ) {
      throw new BadImplementationException(
          'customerService.update.id.null' )
    }

    if ( !dto ) {
      throw new BadImplementationException(
          'customerService.update.dto.null' )
    }

    def instance = findOne( id )
    def client = securityService.getCurrent()

    if ( customerRepository
        .findFirstByClientAndNameAndDateDeletedIsNull(
        client, dto.name ) != null ) {
      throw new BadRequestException( 'customer.create.name.exists' )
    }

    instance.name = dto.name
    customerRepository.save( instance )
    instance

  }

  void delete( Long id ) throws Exception {

    def instance = findOne( id )
    instance.dateDeleted = new Date()
    customerRepository.save( instance )

  }

  Map getFields( Customer customer ) throws Exception {

    if ( !customer ) {
      throw new BadImplementationException(
          'customerService.getFields.customer.null' )
    }

    [ id: customer.id, name: customer.name, dateCreated: customer.dateCreated ]

  }

  private CustomerListDto getFindAllDto( Map params ) throws Exception {

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

    if( params.word ){ dto.word = params.word }

    dto.client = securityService.getCurrent()
    dto

  }

}
