package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.dtos.FinancialInstitutionDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class FinancialInstitutionServiceFindOneSpec extends Specification {

  def service = new FinancialInstitutionService()

  def financialInstitutionRepository = Mock( FinancialInstitutionRepository )

  def setup() {
    service.financialInstitutionRepository = financialInstitutionRepository
  }

  def "invoking method successfully"() {

    when:
      def result = service.findOne( id )
    then:
      1 * financialInstitutionRepository.findOne( _ as Long ) >>
          new FinancialInstitution()
      result instanceof FinancialInstitution
    where:
      id = 1L

  }

  def "parameter 'id' is null"() {

    when:
      service.findOne( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'financialInstitutionService.findOne.id.null'
    where:
      id = null

  }

  def "instance not found"() {

    when:
      service.findOne( id )
    then:
      1 * financialInstitutionRepository.findOne( _ as Long ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'financialInstitution.not.found'
    where:
      id = 1L

  }

}
