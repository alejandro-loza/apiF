
package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.exceptions.*

import spock.lang.Specification

class TransactionsApiServiceFindDuplicatedSpec extends Specification {

  def service = new TransactionsApiService()

  def movementService = Mock( MovementService )
  def restTemplateService = Mock( RestTemplateService )

  def setup() {

    service.movementService = movementService
    service.restTemplateService = restTemplateService

  }

  def "invoking method successfully"() {

    given:
      service.daysDifference = 5
      service.descriptionPercentage = 81
    when:
      def result = service.findDuplicated( movement )
    then:
      1 * movementService.getMovementsToDuplicated( _ as Movement ) >> getListMovements()
      1 * restTemplateService.get( _ as String, _ as Map, _ as Map  ) >> rest
      1 * movementService.updateDuplicated( _ as Movement )
      result == true
    where:
      movement = getMovement( "0",0 )
      rest = [ result: [ results: [ [reason: [data:"numbers"], similarity: [percent:91.5] ] ] ] ]
  }

  def "invoking method successfully (no movments in range of time)"() {

    when:
      def result = service.findDuplicated( movement )
    then:
      1 * movementService.getMovementsToDuplicated( _ as Movement ) >> []
      result == false
    where:
      movement = getMovement( "a",0 )
  }

  def "parameter 'id' is null"() {

    when:
      service.findDuplicated( movement )
    then:
      BadRequestException e = thrown()
      e.message == 'transactionsApi.findDuplicated.movement.null'
    where:
      movement = new Movement()

  }

  def "parameter 'mmovement' is null"() {

    when:
      service.findDuplicated( movement )
    then:
      BadRequestException e = thrown()
      e.message == 'transactionsApi.findDuplicated.movement.null'
    where:
      movement = null

  }

  List getListMovements(){
    def list = []
    5.times{
      def mov = getMovement( "${it}",it ) 
      list << mov  
    }
    list
  }

  Movement getMovement( String id, int min){
    def mov = new Movement(
        id: id,
        account: new Account( 
          id: "b",
          institution: new FinancialInstitution( code: "BNMX") 
          ),
        date: new Date().minus( min ),
        description: "description${id}"
        )
  }

}
