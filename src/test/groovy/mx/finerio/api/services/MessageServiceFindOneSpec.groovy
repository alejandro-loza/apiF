package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.context.MessageSource

import spock.lang.Specification

class MessageServiceFindOneSpec extends Specification {

  def service = new MessageService()

  def messageSource = Mock( MessageSource )

  def setup() {
    service.messageSource = messageSource
  }
  
  def "invoking method successfully"() {

    when:
      def result = service.findOne( message, code )
    then:
      3 * messageSource.getMessage( _ as String, null, null ) >> 'message'
      result instanceof Map
      result.code !=  null
      result.title != null
      result.detail != null
    where:
      message = 'message'
      code = 'code'

  }

  def "parameter 'message' is null"() {

    when:
      service.findOne( message, code )
    then:
      BadImplementationException e = thrown()
      e.message == 'messageService.findOne.message.null'
    where:
      message = null
      code = 'code'

  }

  def "parameter 'message' is blank"() {

    when:
      service.findOne( message, code )
    then:
      BadImplementationException e = thrown()
      e.message == 'messageService.findOne.message.null'
    where:
      message = ''
      code = 'code'

  }

  def "parameter 'code' is null"() {

    when:
      def result = service.findOne( message, code )
    then:
      2 * messageSource.getMessage( _ as String, null, null ) >> 'message'
      result instanceof Map
      result.code !=  null
      result.title != null
      result.detail != null
    where:
      message = 'message'
      code = null

  }

  def "parameter 'code' is blank"() {

    when:
      def result = service.findOne( message, code )
    then:
      2 * messageSource.getMessage( _ as String, null, null ) >> 'message'
      result instanceof Map
      result.code !=  null
      result.title != null
      result.detail != null
    where:
      message = 'message'
      code = ''

  }

}
