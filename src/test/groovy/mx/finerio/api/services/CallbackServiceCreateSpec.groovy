package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.repository.CallbackRepository
import mx.finerio.api.dtos.CallbackDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException

import spock.lang.Specification

class CallbackServiceCreateSpec extends Specification {

  def service = new CallbackService()

  def securityService = Mock( SecurityService )
  def callbackRepository = Mock( CallbackRepository )

  def setup() {

    service.securityService = securityService
    service.callbackRepository = callbackRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.create( callbackDto )
    then:
      1 * securityService.getCurrent() >> new Client()
      1 * callbackRepository.findByClientAndNature(
          _ as Client, _ as Callback.Nature )
      1 * callbackRepository.save( _ as Callback ) >> new Callback()
      result instanceof Callback
    where:
      callbackDto = new CallbackDto( nature: Callback.Nature.SUCCESS )

  }

  def "parameter 'callbackDto' is null"() {

    when:
      service.create( callbackDto )
    then:
      BadImplementationException e = thrown()
      e.message == 'callbackService.create.callbackDto.null'
    where:
      callbackDto = null

  }

  def "instance already exists"() {

    when:
      service.create( callbackDto )
    then:
      1 * securityService.getCurrent() >> new Client()
      1 * callbackRepository.findByClientAndNature(
          _ as Client, _ as Callback.Nature ) >> new Callback()
      BadRequestException e = thrown()
      e.message == 'callback.create.exists'
    where:
      callbackDto = new CallbackDto( nature: Callback.Nature.SUCCESS )

  }

}
