package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class CategorizerServiceSearchSpec extends Specification {

  def service = new CategorizerService()

  def restTemplateService = Mock( RestTemplateService )

  def setup() {

    service.restTemplateService = restTemplateService
    service.url = 'url'
    service.username = 'username'
    service.password = 'password'

  }
 
  def "everything is OK"() {

    when:
      def result = service.search( text ) 
    then:
      1 * restTemplateService.get( _ as String, _ as Map, _ as Map ) >>
          [ result: 'hello world' ]
      result instanceof Map
      result.result != null
    where:
      text = 'text'

  }

  def "parameter 'text' is null"() {

    when:
      service.search( text ) 
    then:
      BadImplementationException e = thrown()
      e.message == 'categorizerService.search.text.null'
    where:
      text = null

  }

  def "parameter 'text' is blank"() {

    when:
      service.search( text ) 
    then:
      BadImplementationException e = thrown()
      e.message == 'categorizerService.search.text.null'
    where:
      text = ''

  }

}
