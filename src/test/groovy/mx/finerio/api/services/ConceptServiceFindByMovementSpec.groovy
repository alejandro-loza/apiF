package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.exceptions.*

import spock.lang.Specification

class ConceptServiceFindByMovementSpec extends Specification {

  def service = new ConceptService()

  def conceptRepository = Mock( ConceptRepository )

  def setup() {

    service.conceptRepository = conceptRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findByMovement( movement )
    then:
      1 * conceptRepository.findByMovement( _ as Movement ) >> new Concept( id: "uuid" )
      result instanceof Concept
    where:
      movement = new Movement( id: "uuid" )

  }

  def "concept not found"() {

    when:
      service.findByMovement( movement )
    then:
      1 * conceptRepository.findByMovement( _ as Movement ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'concept.not.found'
    where:
      movement = new Movement( id: "uuid" )

  }

  def "parameter 'movement' is null"() {

    when:
      service.findByMovement( movement )
    then:
      BadImplementationException e = thrown()
      e.message == 'conceptService.findByMovement.movement.null'
    where:
      movement = null

  }

}
