package mx.finerio.api.services

import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.FinancialInstitution.Status
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.services.imp.FinancialInstitutionServiceImp

import spock.lang.Specification

class FinancialInstitutionServiceGetFieldsSpec extends Specification {

  def service = new FinancialInstitutionServiceImp()

  def "invoking method successfully"() {

    when:
      def result = service.getFields( financialInstitution )
    then:
      result instanceof Map
      result.id != null
      result.name != null
      result.status != null
    where:
      financialInstitution = getFinancialInstitution()

  }

  def "invoking method successfully (status = PARTIALLY_ACTIVE)"() {

    given:
      def financialInstitution = getFinancialInstitution()
      financialInstitution.status = Status.PARTIALLY_ACTIVE
    when:
      def result = service.getFields( financialInstitution )
    then:
      result instanceof Map
      result.id != null
      result.name != null
      result.status == Status.ACTIVE

  }

  def "parameter 'financialInstitution' is null"() {

    when:
      service.getFields( financialInstitution )
    then:
      BadImplementationException e = thrown()
      e.message == 'financialInstitutionService.getFields.financialInstitution.null'
    where:
      financialInstitution = null

  }

  private FinancialInstitution getFinancialInstitution() throws Exception {

    new FinancialInstitution(
      id: 1L,
      name: 'name',
      status: 'ACTIVE'
    )

  }

}
