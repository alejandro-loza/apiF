package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CustomerRepository
import mx.finerio.api.dtos.CustomerDto
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class CustomerServiceCreateSpec extends Specification {

  def service = new CustomerService()

  def securityService = Mock( SecurityService )
  def customerRepository = Mock( CustomerRepository )
  def adminService = Mock( AdminService )

  def setup() {

    service.securityService = securityService
    service.customerRepository = customerRepository
    service.adminService = adminService

  }

  def "invoking method successfully"() {

    when:
      def result = service.create( dto )
    then:
      1 * securityService.getCurrent() >> new Client()
      1 * customerRepository.save( _ as Customer ) >> new Customer()
      result instanceof Customer
    where:
      dto = new CustomerDto( name: 'Customer name' )

  }

  def "parameter 'dto' is null"() {

    when:
      service.create( dto )
    then:
      BadImplementationException e = thrown()
      e.message == 'customerService.create.dto.null'
    where:
      dto = null

  }

}
