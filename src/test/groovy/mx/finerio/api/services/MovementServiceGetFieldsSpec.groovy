package mx.finerio.api.services

import mx.finerio.api.domain.Movement
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class MovementServiceGetFieldsSpec extends Specification {

  def service = new MovementService()

  def "invoking method successfully"() {

    when:
      def result = service.getFields( movement )
    then:
      result instanceof Map
      result.id != null
      result.description != null
      result.amount != null
      result.type != null
      result.dateCreated != null
    where:
      movement = getMovement()

  }

  def "parameter 'movement' is null"() {

    when:
      service.getFields( movement )
    then:
      BadImplementationException e = thrown()
      e.message == 'movementService.getFields.movement.null'
    where:
      movement = null

  }

  private Movement getMovement() throws Exception {

    new Movement(
      id: 1L,
      description: 'description',
      amount: 1.00,
      type: Movement.Type.CHARGE,
      customDate: new Date()
    )

  }

}
