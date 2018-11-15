package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.exceptions.*

import spock.lang.Specification

class TransactionPostProcessorServiceProcessDuplicatedSpec extends Specification {

  def service = new TransactionPostProcessorService()

  def movementService = Mock( MovementService )
  def atmId = 'atmId'

  def setup() {

    service.movementService = movementService
    service.atmId = atmId

  }

  def "invoking method successfully (credit card income)"() {

    when:
      service.processDuplicated( movement )
    then:
      1 * movementService.updateDuplicated( _ as Movement )
    where:
      movement = new Movement( type: Movement.Type.DEPOSIT,
          account: new Account( nature: "Cr\u00E9dito" ) )

  }

  @spock.lang.Ignore
  def "invoking method successfully (atm outcome)"() {

    when:
      service.processDuplicated( movement )
    then:
      1 * movementService.updateDuplicated( _ as Movement )
    where:
      movement = new Movement( type: Movement.Type.CHARGE,
      category: new Category( id: atmId ) )

  }

  def "invoking method successfully (not duplicated)"() {

    when:
      service.processDuplicated( movement )
    then:
      0 * movementService.updateDuplicated( _ as Movement )
    where:
      movement = new Movement()

  }

  def "parameter 'movement' is null"() {

    when:
      service.processDuplicated( movement )
    then:
      BadImplementationException e = thrown()
      e.message == 'transactionPostProcessor.processDuplicated.movement.null'
    where:
      movement = null

  }

}
