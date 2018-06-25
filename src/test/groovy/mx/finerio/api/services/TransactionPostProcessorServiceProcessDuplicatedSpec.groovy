package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.exceptions.*

import spock.lang.Specification

class TransactionPostProcessorServiceProcessDuplicatedSpec extends Specification {

  def service = new TransactionPostProcessorService()

  def movementService = Mock( MovementService )
  def conceptService = Mock( ConceptService )
  def credentialService = Mock( CredentialService )

  def setup() {

    service.movementService = movementService
    service.conceptService = conceptService
    service.credentialService = credentialService

  }

  def "invoking method successfully"() {

    when:
      def result = service.processDuplicated( movement )
    then:
      1 * movementService.findOne( _ as String ) >>
        new Movement( id: "uuid", account: account, type: Movement.Type.DEPOSIT )
      1 * conceptService.findByMovement( _ as Movement ) >> new Concept( id: "uuid" )
      1 * movementService.updateDuplicated( _ as Movement ) >>
        new Movement( 
            id: "uuid", 
            account: account, 
            type: Movement.Type.DEPOSIT,
            duplicated: false)
      result instanceof Movement
      result.duplicated == false
    where:
      movement = new Movement( id: "uuid" )
      account = new Account( id: "uuid", nature: "Crédito" )

  }

  def "invoking method successfully (no DEPOSIT)"() {

    when:
      def result = service.processDuplicated( movement )
    then:
      1 * movementService.findOne( _ as String ) >>
        new Movement( id: "uuid", account: account )
      1 * conceptService.findByMovement( _ as Movement ) >> new Concept( id: "uuid" )
      result instanceof Movement
    where:
      movement = new Movement( id: "uuid" )
      account = new Account( id: "uuid" )

  }

  def "invoking method successfully (no duplicated)"() {

    when:
      def result = service.processDuplicated( movement )
    then:
      1 * movementService.findOne( _ as String ) >>
        new Movement( id: "uuid", account: account )
      1 * conceptService.findByMovement( _ as Movement ) >> new Concept( id: "uuid" )
      result instanceof Movement
    where:
      movement = new Movement( id: "uuid" )
      account = new Account( id: "uuid" )

  }

  def "parameter 'movement' is null"() {

    when:
      service.processDuplicated( movement )
    then:
      BadRequestException e = thrown()
      e.message == 'transactionPostProcessor.processDuplicated.movement.null'
    where:
      movement = null

  }

}
