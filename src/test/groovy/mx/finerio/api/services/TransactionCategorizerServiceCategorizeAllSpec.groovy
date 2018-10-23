package mx.finerio.api.services

import mx.finerio.api.domain.Movement
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class TransactionCategorizerServiceCategorizeAllSpec extends Specification {

  def service = new TransactionCategorizerService()

  def movementService = Mock( MovementService )
  def transactionPostProcessorService = Mock( TransactionPostProcessorService )

  def setup() {

    service.movementService = movementService
    service.transactionPostProcessorService = transactionPostProcessorService

  }

  def "invoking method successfully"() {

    given:
      service.maxThreads = 5
    when:
      service.categorizeAll( movements )
    then:
      true
    where:
      movements = getMovements()

  }

  def "parameter 'movements' is null"() {

    when:
      service.categorizeAll( movements )
    then:
      BadImplementationException e = thrown()
      e.message ==
          'transactionCategorizerService.ctagorizeAll.movements.null'
    where:
      movements = null

  }

  private List getMovements() throws Exception {

    [
      getMovement(),
      getMovement(),
      getMovement()
    ]

  }

  private Movement getMovement() throws Exception {
    new Movement()
  }

}
