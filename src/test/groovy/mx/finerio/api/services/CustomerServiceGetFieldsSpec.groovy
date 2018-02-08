package mx.finerio.api.services

import mx.finerio.api.domain.Customer
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class CustomerServiceGetFieldsSpec extends Specification {

  def service = new CustomerService()

  def "invoking method successfully"() {

    when:
      def result = service.getFields( customer )
    then:
      result instanceof Map
      result.id != null
      result.name != null
      result.dateCreated != null
    where:
      customer = getCustomer()

  }

  def "parameter 'customer' is null"() {

    when:
      service.getFields( customer )
    then:
      BadImplementationException e = thrown()
      e.message == 'customerService.getFields.customer.null'
    where:
      customer = null

  }

  private Customer getCustomer() throws Exception {

    new Customer(
      id: 1L,
      name: 'name',
      dateCreated: new Date()
    )

  }

}
