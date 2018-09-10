package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.exceptions.*

import spock.lang.Specification

class MovementServiceUpdateDuplicatedSpec extends Specification {

  def service = new MovementService()

  def movementRepository = Mock( MovementRepository )
  def accountService = Mock( AccountService )

  def setup() {

    service.movementRepository = movementRepository
    service.accountService = accountService

  }

  def "invoking method successfully"() {
    
    when:
      service.updateDuplicated( movement )
    then:
      1 * movementRepository.save( _ as Movement )
      movement.duplicated == true
    where:
      movement = new Movement()

  }

  def "parameter 'movement' is null"() {

    when:
      service.updateDuplicated( movement )
    then:
      BadImplementationException e = thrown()
      e.message == 'movementService.updateDuplicated.movement.null'
    where:
      movement = null

  }

}
