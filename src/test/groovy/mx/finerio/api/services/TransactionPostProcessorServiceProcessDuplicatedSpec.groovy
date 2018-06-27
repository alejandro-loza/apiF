package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.exceptions.*

import spock.lang.Specification

class TransactionPostProcessorServiceProcessDuplicatedSpec extends Specification {

  def service = new TransactionPostProcessorService()

  def movementService = Mock( MovementService )
  def conceptService = Mock( ConceptService )

  def setup() {

    service.movementService = movementService
    service.conceptService = conceptService

  }

  def "invoking method successfully (DEPOSIT)"() {

    when:
      def result = service.processDuplicated( movement )
    then:
      1 * movementService.findOne( _ as String ) >>
        new Movement( id: "uuid", account: account, type: Movement.Type.DEPOSIT )
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

  def "invoking method successfully (CHARGE)"() {

    when:
      def result = service.processDuplicated( movement )
    then:
      1 * movementService.findOne( _ as String ) >>
        new Movement( id: "uuid", account: account, type: Movement.Type.CHARGE )
      1 * conceptService.findByMovement( _ as Movement ) >> 
        new Concept( id: "uuid", category: new Category( name:"Cajero automático" ) )
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

  def "CHARGE and 'category' != 'Cajero automático'"() {

    when:
      def result = service.processDuplicated( movement )
    then:
      1 * movementService.findOne( _ as String ) >>
        new Movement( id: "uuid", account: account, type: Movement.Type.CHARGE )
      1 * conceptService.findByMovement( _ as Movement ) >> 
        new Concept( id: "uuid", category: new Category( name:"Cajero" ) )
      result instanceof Movement
    where:
      movement = new Movement( id: "uuid" )
      account = new Account( id: "uuid", nature: "Crédito" )

  }

  def "CHARGE and 'category' is null"() {

    when:
      def result = service.processDuplicated( movement )
    then:
      1 * movementService.findOne( _ as String ) >>
        new Movement( id: "uuid", account: account, type: Movement.Type.CHARGE )
      1 * conceptService.findByMovement( _ as Movement ) >> new Concept( id: "uuid" )
      result instanceof Movement
    where:
      movement = new Movement( id: "uuid" )
      account = new Account( id: "uuid", nature: "Crédito" )

  }

  def "DEPOSIT and 'nature' is null"() {

    when:
      def result = service.processDuplicated( movement )
    then:
      1 * movementService.findOne( _ as String ) >>
        new Movement( id: "uuid", account: account, type: Movement.Type.DEPOSIT )
      result instanceof Movement
    where:
      movement = new Movement( id: "uuid" )
      account = new Account( id: "uuid" )

  }

  def "DEPOSIT and 'nature' != Crédito"() {

    when:
      def result = service.processDuplicated( movement )
    then:
      1 * movementService.findOne( _ as String ) >>
        new Movement( id: "uuid", account: account, type: Movement.Type.DEPOSIT )
      result instanceof Movement
    where:
      movement = new Movement( id: "uuid" )
      account = new Account( id: "uuid", nature: "Ahorro" )

  }

  def "parameter type' is null"() {

    when:
      def result = service.processDuplicated( movement )
    then:
      1 * movementService.findOne( _ as String ) >>
        new Movement( id: "uuid", account: account )
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
