package mx.finerio.api.services

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.Movement
import mx.finerio.api.domain.repository.MovementRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class MovementServiceFindOneSpec extends Specification {

  def service = new MovementService()

  def accountService = Mock( AccountService )
  def movementRepository = Mock( MovementRepository )

  def setup() {

    service.accountService = accountService
    service.movementRepository = movementRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findOne( id )
    then:
      1 * accountService.findOne( _ as String ) >> account
      1 * movementRepository.findOne( _ as String ) >>
          new Movement( account: account )
      result instanceof Movement
    where:
      id = 'uuid'
      account = new Account( id: 'id' )

  }

  def "parameter 'id' is null"() {

    when:
      service.findOne( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'movementService.findOne.id.null'
    where:
      id = null

  }

  def "parameter 'id' is blank"() {

    when:
      service.findOne( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'movementService.findOne.id.null'
    where:
      id = ''

  }

  def "instance not found"() {

    when:
      service.findOne( id )
    then:
      1 * movementRepository.findOne( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'movement.not.found'
    where:
      id = 'uuid'

  }

  def "instance not found (account not found)"() {

    when:
      service.findOne( id )
    then:
      1 * accountService.findOne( _ as String ) >> {
          throw new InstanceNotFoundException( 'error' ) }
      1 * movementRepository.findOne( _ as String ) >>
          new Movement( account: account )
      InstanceNotFoundException e = thrown()
      e.message == 'movement.not.found'
    where:
      id = 'uuid'
      account = new Account( id: 'id' )

  }

  def "instance not found (dateDeleted is not null)"() {

    when:
      service.findOne( id )
    then:
      1 * movementRepository.findOne( _ as String ) >>
          new Movement( dateDeleted: new Date() )
      InstanceNotFoundException e = thrown()
      e.message == 'movement.not.found'
    where:
      id = 'uuid'

  }

}
