package mx.finerio.api.services

import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.FinancialInstitutionRepository

import spock.lang.Specification

class FinancialInstitutionServiceFindAllSpec extends Specification {

  def service = new FinancialInstitutionService()

  def financialInstitutionRepository = Mock( FinancialInstitutionRepository )

  def setup() {
    service.financialInstitutionRepository = financialInstitutionRepository
  }

  def "invoking method successfully"() {

    when:
      def result = service.findAll()
    then:
      1 * financialInstitutionRepository.findAll() >>
        [
          new FinancialInstitution( code: 'BBVA' ),
          new FinancialInstitution( code: 'DINERIO' ),
          new FinancialInstitution( code: 'BNMX' ) 
        ]
      result instanceof Map
      result.nextCursor == null
      result.data instanceof List
      result.data.size() == 2
      result.data[ 1 ] instanceof FinancialInstitution

  }

}
