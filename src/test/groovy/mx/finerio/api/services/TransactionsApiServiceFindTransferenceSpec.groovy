package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.exceptions.*

import spock.lang.Specification

class TransactionsApiServiceFindTransferenceSpec extends Specification {

  def service = new TransactionsApiService()

  def movementService = Mock( MovementService )
  def restTemplateService = Mock( RestTemplateService )

  def setup() {

    service.movementService = movementService
    service.restTemplateService = restTemplateService

  }

  def "invoking method successfully"() {

    when:
      def result = service.findTransference( map )
    then:
      1 * restTemplateService.get( _ as String, _ as Map, _ as Map  ) >> rest
      result instanceof List
    where:
      map = [ list: ["a","b"], bank:"BNMX", type: "DEPOSIT" ]
      rest = [ result: [ results: ["a","b"] ] ]
  }

  def "parameter 'list' is null"() {

    when:
      service.findTransference( map )
    then:
      BadRequestException e = thrown()
      e.message == 'transactionsApi.findTransference.list.null'
    where:
      map = [ bank:"BNMX", type:"DEPOSIT" ]

  }

  def "parameter 'type' is null"() {

    when:
      service.findTransference( map )
    then:
      BadRequestException e = thrown()
      e.message == 'transactionsApi.findTransference.type.null'
    where:
      map = [ list: ["a","b"], bank:"BNMX" ]

  }

  def "parameter 'bank' is null"() {

    when:
      service.findTransference( map )
    then:
      BadRequestException e = thrown()
      e.message == 'transactionsApi.findTransference.bank.null'
    where:
      map = [ list: ["a","b"], type:"DEPOSIT" ]

  }

  def "parameter 'map' is null"() {

    when:
      service.findTransference( map )
    then:
      BadRequestException e = thrown()
      e.message == 'transactionsApi.findTransference.map.null'
    where:
      map = null

  }

}
