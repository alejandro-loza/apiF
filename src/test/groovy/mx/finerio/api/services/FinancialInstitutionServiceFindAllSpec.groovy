package mx.finerio.api.services

import mx.finerio.api.domain.Country
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.FinancialIntitutionSpecs
import mx.finerio.api.domain.repository.CountryRepository
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.dtos.FinancialInstitutionListDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException

import org.springframework.data.jpa.domain.Specification as Spec

import spock.lang.Specification
import mx.finerio.api.services.imp.FinancialInstitutionServiceImp

class FinancialInstitutionServiceFindAllSpec extends Specification {

  def service = new FinancialInstitutionServiceImp()

  def listService = Mock( ListService )
  def countryRepository = Mock( CountryRepository )
  def financialInstitutionRepository =
      Mock( FinancialInstitutionRepository )

  def setup() {

    service.listService = listService
    service.countryRepository = countryRepository
    service.financialInstitutionRepository = financialInstitutionRepository

  }

  def "invoking method successfully"() {

    given:
      def params = [:]
    when:
      def result = service.findAll( params )
    then:
      1 * listService.validateFindAllDto( _ as FinancialInstitutionListDto,
          _ as Map )
      1 * countryRepository.findOneByCode( _ as String ) >> new Country()
      1 * listService.findAll( _ as FinancialInstitutionListDto,
          _ as FinancialInstitutionRepository, _ as Spec ) >>
        [
          data: [
            new FinancialInstitution( code: 'BBVA' ),
            new FinancialInstitution( code: 'DINERIO' ),
            new FinancialInstitution( code: 'BNMX' )
          ],
        nextCursor: null
      ]
      result instanceof Map
      result.nextCursor == null
      result.data instanceof List
      result.data.size() == 3
      result.data[ 2 ] instanceof FinancialInstitution

  }

  def "invoking method successfully (country)"() {

    given:
      def params = [ country: 'CO' ]
    when:
      def result = service.findAll( params )
    then:
      1 * listService.validateFindAllDto( _ as FinancialInstitutionListDto,
          _ as Map )
      1 * countryRepository.findOneByCode( _ as String ) >> new Country()
      1 * listService.findAll( _ as FinancialInstitutionListDto,
          _ as FinancialInstitutionRepository, _ as Spec ) >>
        [
          data: [
            new FinancialInstitution( code: 'BBVA' ),
            new FinancialInstitution( code: 'DINERIO' ),
            new FinancialInstitution( code: 'BNMX' )
          ],
        nextCursor: null
      ]
      result instanceof Map
      result.nextCursor == null
      result.data instanceof List
      result.data.size() == 3
      result.data[ 2 ] instanceof FinancialInstitution

  }

  def "Wrong country"() {

    given:
      def params = [ country: 'wrong' ]
    when:
      service.findAll( params )
    then:
      BadRequestException e = thrown()
      e.message == 'country.not.found'

  }

  def "invoking method successfully (bank type)"() {

    given:
      def params = [ type: 'business' ]
    when:
      def result = service.findAll( params )
    then:
      1 * listService.validateFindAllDto( _ as FinancialInstitutionListDto,
          _ as Map )
      1 * countryRepository.findOneByCode( _ as String ) >> new Country()
      1 * listService.findAll( _ as FinancialInstitutionListDto,
          _ as FinancialInstitutionRepository, _ as Spec ) >>
        [
          data: [
            new FinancialInstitution( code: 'BBVA' ),
            new FinancialInstitution( code: 'DINERIO' ),
            new FinancialInstitution( code: 'BNMX' )
          ],
        nextCursor: null
      ]
      result instanceof Map
      result.nextCursor == null
      result.data instanceof List
      result.data.size() == 3
      result.data[ 2 ] instanceof FinancialInstitution

  }

  def "Wrong bank type"() {

    given:
      def params = [ type: 'wrong' ]
    when:
      service.findAll( params )
    then:
      BadRequestException e = thrown()
      e.message == 'financialInstitution.type.not.found'

  }

  def "parameter 'params' is null"() {

    given:
      def params = null
    when:
      service.findAll( params )
    then:
      BadImplementationException e = thrown()
      e.message == 'financialInstitutionService.findAll.params.null'

  }

}
