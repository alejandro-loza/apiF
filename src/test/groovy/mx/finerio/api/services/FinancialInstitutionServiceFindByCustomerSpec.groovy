package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.services.imp.FinancialInstitutionServiceImp
import spock.lang.Specification

import mx.finerio.api.exceptions.InstanceNotFoundException


class FinancialInstitutionServiceFindByCustomerSpec extends Specification {

  def service = new FinancialInstitutionServiceImp()


  def setup() {
    service.financialInstitutionRepository =  Mock( FinancialInstitutionRepository )
    service.customerService = Mock(CustomerService)
    service.securityService = Mock(SecurityService)
  }

  def "Should get an financial institution by customer"(){

    given:
    def client = new Client()

    Customer customer = new Customer()
    customer.with {
      customer.client =  client
    }

    when:
    def response = service.getByIdAndCustomer(1, customer)

    then :
    1 * service.securityService.getCurrent() >> client
    1 * service.financialInstitutionRepository.findByIdAndCustomerAndDateDeletedIsNull(_ as Long, _ as Customer) >> new FinancialInstitution()

    assert response instanceof FinancialInstitution

  }

  def "Should thore not found found account on not matching "(){

    when:
    service.getByIdAndCustomer(1, new Customer())

    then :
    1 * service.securityService.getCurrent() >> new Client()

    then:

    InstanceNotFoundException e = thrown()
    e.message == 'account.notFound'

  }

  def "Should throw exception on an financial institution by customer null "(){

    given:
    def client = new Client()

    Customer customer = new Customer()
    customer.with {
      customer.client =  client
    }

    when:
    service.getByIdAndCustomer(1, customer)

    then :
    1 * service.securityService.getCurrent() >> client
    1 * service.financialInstitutionRepository.findByIdAndCustomerAndDateDeletedIsNull(_ as Long, _ as Customer) >> null

    then:

    InstanceNotFoundException e = thrown()
    e.message == 'financialInstitution.not.found'

  }


}
