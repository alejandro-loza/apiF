package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.repository.CallbackRepository
import mx.finerio.api.dtos.CallbackUpdateDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException

import spock.lang.Specification

class CallbackServiceUpdateSpec extends Specification {

  def service = new CallbackService()

  def securityService = Mock( SecurityService )
  def callbackRepository = Mock( CallbackRepository )

  def setup() {

    service.securityService = securityService
    service.callbackRepository = callbackRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.update( id, callbackUpdateDto )
    then:
      1 * securityService.getCurrent() >> client
      1 * callbackRepository.findOne( _ as Long ) >>
          new Callback( client: client )
      1 * callbackRepository.save( _ as Callback ) >> new Callback()
      result instanceof Callback
    where:
      id = 1L
      callbackUpdateDto = new CallbackUpdateDto( url: 'newUrl' )
      client = new Client( id: 1L )

  }

  def "parameter 'id' is null"() {

    when:
      service.update( id, callbackUpdateDto )
    then:
      BadImplementationException e = thrown()
      e.message == 'callbackService.update.id.null'
    where:
      id = null
      callbackUpdateDto = new CallbackUpdateDto( url: 'newUrl' )

  }

  def "parameter 'callbackUpdateDto' is null"() {

    when:
      service.update( id, callbackUpdateDto )
    then:
      BadImplementationException e = thrown()
      e.message == 'callbackService.update.callbackUpdateDto.null'
    where:
      id = 1L
      callbackUpdateDto = null

  }

}
