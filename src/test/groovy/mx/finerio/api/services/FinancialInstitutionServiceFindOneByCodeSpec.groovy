package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.dtos.FinancialInstitutionDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.services.imp.FinancialInstitutionServiceImp

import spock.lang.Specification

class FinancialInstitutionServiceFindOneByCodeSpec extends Specification {

  def service = new FinancialInstitutionServiceImp()

  def financialInstitutionRepository = Mock( FinancialInstitutionRepository )

  def setup() {
    service.financialInstitutionRepository = financialInstitutionRepository
  }

  def "invoking method successfully"() {

    when:
      def result = service.findOneByCode( code )
    then:
      1 * financialInstitutionRepository.findOneByCode( _ as String ) >>
          new FinancialInstitution()
      result instanceof FinancialInstitution
    where:
      code = 'bnx'

  }

  def "parameter 'code' is null"() {

    when:
      service.findOneByCode( code )
    then:
      BadImplementationException e = thrown()
      e.message == 'financialInstitutionService.findOneByCode.code.null'
    where:
      code = null

  }

  def "instance not found"() {

    when:
      service.findOneByCode( code )
    then:
         1 * financialInstitutionRepository.findOneByCode( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'financialInstitution.not.found'
    where:
       code = 'bnx'

  }

}