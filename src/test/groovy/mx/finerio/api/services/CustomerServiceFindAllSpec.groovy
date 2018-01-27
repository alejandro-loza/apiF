package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CustomerRepository
import mx.finerio.api.dtos.CustomerListDto
import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

import spock.lang.Specification

class CustomerServiceFindAllSpec extends Specification {

  def service = new CustomerService()

  def securityService = Mock( SecurityService )
  def customerRepository = Mock( CustomerRepository )

  def setup() {

    service.securityService = securityService
    service.customerRepository = customerRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findAll( dto )
    then:
      1 * securityService.getCurrent() >> new Client()
      1 * customerRepository.findAll( _ as Object, _ as PageRequest ) >>
          new PageImpl( [ new Customer(), new Customer() ] )
      result instanceof Map
      result.next == null
      result.data instanceof List
      result.data.size() == 2
      result.data[ 0 ] instanceof Customer
    where:
      dto = new CustomerListDto()

  }

  def "parameter 'dto' is null"() {

    when:
      service.findAll( dto )
    then:
      BadImplementationException e = thrown()
      e.message == 'customerService.findAll.dto.null'
    where:
      dto = null

  }

}
