package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.dtos.FinancialInstitutionDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification
import mx.finerio.api.services.imp.FinancialInstitutionServiceImp

class FinancialInstitutionServiceFindOneAndValidateSpec extends Specification {

  def service = new FinancialInstitutionServiceImp()

  def financialInstitutionRepository = Mock( FinancialInstitutionRepository )

  def setup() {
    service.financialInstitutionRepository = financialInstitutionRepository
  }

  def "invoking method successfully"() {

    when:
      def result = service.findOneAndValidate( id )
    then:
      1 * financialInstitutionRepository.findOne( _ as Long ) >>
          new FinancialInstitution( status: 'ACTIVE' )
      result instanceof FinancialInstitution
    where:
      id = 1L

  }

  def "invoking method successfully PARTIALLY_ACTIVE"() {

    when:
    def result = service.findOneAndValidate( id )
    then:
    1 * financialInstitutionRepository.findOne( _ as Long ) >>
            new FinancialInstitution( status: 'PARTIALLY_ACTIVE' )
    result instanceof FinancialInstitution
    where:
    id = 1L

  }

  def "financial institution is not active"() {

    when:
      service.findOneAndValidate( id )
    then:
      1 * financialInstitutionRepository.findOne( _ as Long ) >>
          new FinancialInstitution( status: 'INACTIVE' )
      BadRequestException e = thrown()
      e.message == 'financialInstitution.disabled'
    where:
      id = 1L

  }

  def "parameter 'id' is null"() {

    when:
      service.findOneAndValidate( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'financialInstitutionService.findOneAndValidate.id.null'
    where:
      id = null

  }

  def "instance not found"() {

    when:
      service.findOneAndValidate( id )
    then:
      1 * financialInstitutionRepository.findOne( _ as Long ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'financialInstitution.not.found'
    where:
      id = 1L

  }

}
