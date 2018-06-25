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
      def result = service.updateDuplicated( movement )
    then:
      1 * movementRepository.findOne( _ as String ) >>
        new Movement( 
              id: "uuid",
              duplicated:false,
              account: new Account(id:"id")
              )
      1 * accountService.findOne( _ as String ) >> new Account(id:"id")
      1 * movementRepository.save( _ as Movement ) >>
          new Movement( 
              id: "uuid",
              duplicated:true,
              lastUpdated: new Date()
              )
      result instanceof Movement
      result.duplicated == true
      result.lastUpdated == new Date()
    where:
      movement = new Movement( id: 'uuid', account: new Account( id: 'id' ) )

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
