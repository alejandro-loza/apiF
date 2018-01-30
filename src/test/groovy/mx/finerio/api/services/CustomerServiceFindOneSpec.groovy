package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CustomerRepository
import mx.finerio.api.dtos.CustomerDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CustomerServiceFindOneSpec extends Specification {

  def service = new CustomerService()

  def securityService = Mock( SecurityService )
  def customerRepository = Mock( CustomerRepository )

  def setup() {

    service.securityService = securityService
    service.customerRepository = customerRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findOne( id )
    then:
      1 * securityService.getCurrent() >> new Client( id: 1 )
      1 * customerRepository.findOne( _ as Long ) >>
          new Customer( client: new Client( id: 1 ) )
      result instanceof Customer
    where:
      id = 1L

  }

  def "parameter 'id' is null"() {

    when:
      service.findOne( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'customerService.findOne.id.null'
    where:
      id = null

  }

  def "instance not found"() {

    when:
      service.findOne( id )
    then:
      1 * securityService.getCurrent() >> new Client( id: 1 )
      1 * customerRepository.findOne( _ as Long ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'customer.not.found'
    where:
      id = 1L

  }

  def "instance not found (different client)"() {

    when:
      service.findOne( id )
    then:
      1 * securityService.getCurrent() >> new Client( id: 2 )
      1 * customerRepository.findOne( _ as Long ) >>
          new Customer( client: new Client( id: 1 ) )
      InstanceNotFoundException e = thrown()
      e.message == 'customer.not.found'
    where:
      id = 1L

  }

}
