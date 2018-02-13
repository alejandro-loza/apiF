package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.repository.CallbackRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CallbackServiceFindOneSpec extends Specification {

  def service = new CallbackService()

  def securityService = Mock( SecurityService )
  def callbackRepository = Mock( CallbackRepository )

  def setup() {

    service.securityService = securityService
    service.callbackRepository = callbackRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findOne( id )
    then:
      1 * securityService.getCurrent() >> new Client( id: 1 )
      1 * callbackRepository.findOne( _ as Long ) >>
          new Callback( client: new Client( id: 1 ) )
      result instanceof Callback
    where:
      id = 1L

  }

  def "parameter 'id' is null"() {

    when:
      service.findOne( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'callbackService.findOne.id.null'
    where:
      id = null

  }

  def "instance not found"() {

    when:
      service.findOne( id )
    then:
      1 * securityService.getCurrent() >> new Client( id: 1 )
      1 * callbackRepository.findOne( _ as Long ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'callback.not.found'
    where:
      id = 1L

  }

  def "instance not found (different client)"() {

    when:
      service.findOne( id )
    then:
      1 * securityService.getCurrent() >> new Client( id: 2 )
      1 * callbackRepository.findOne( _ as Long ) >>
          new Callback( client: new Client( id: 1 ) )
      InstanceNotFoundException e = thrown()
      e.message == 'callback.not.found'
    where:
      id = 1L

  }

}
