package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CustomerRepository
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CustomerServiceCreateSpec extends Specification {

  def service = new CustomerService()

  def securityService = Mock( SecurityService )
  def customerRepository = Mock( CustomerRepository )

  def setup() {

    service.securityService = securityService
    service.customerRepository = customerRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.create( params )
    then:
      1 * securityService.getCurrent() >> new Client()
      1 * customerRepository.save( _ as Customer ) >> new Customer()
      result instanceof Customer
    where:
      params = [ name: 'Customer name' ]

  }

  def "parameter 'params' is null"() {

    when:
      service.create( params )
    then:
      IllegalArgumentException e = thrown()
      e.message == 'customerService.create.params.null'
    where:
      params = null

  }

  def "parameter 'params' is empty"() {

    when:
      service.create( params )
    then:
      IllegalArgumentException e = thrown()
      e.message == 'customerService.create.params.empty'
    where:
      params = [:]

  }

}
