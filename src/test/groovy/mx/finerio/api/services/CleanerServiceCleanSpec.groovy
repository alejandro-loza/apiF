package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class CleanerServiceCleanSpec extends Specification {

  def service = new CleanerService()

  def restTemplateService = Mock( RestTemplateService )

  def setup() {

    service.restTemplateService = restTemplateService
    service.url = 'url'
    service.username = 'username'
    service.password = 'password'

  }
 
  def "everything is OK"() {

    when:
      def result = service.clean( text, income ) 
    then:
      1 * restTemplateService.get( _ as String, _ as Map, _ as Map ) >>
          [ result: 'hello world' ]
      result instanceof String
      result == 'hello world'
    where:
      text = 'text'
      income = false

  }

  def "parameter 'income' is null"() {

    when:
      service.clean( text, income ) 
    then:
      BadImplementationException e = thrown()
      e.message == 'cleanerService.clean.income.null'
    where:
      text = 'text'
      income = null

  }

  def "parameter 'text' is null"() {

    when:
      service.clean( text, income ) 
    then:
      BadImplementationException e = thrown()
      e.message == 'cleanerService.clean.text.null'
    where:
      text = null
      income = false

  }

  def "parameter 'text' is blank"() {

    when:
      service.clean( text, income ) 
    then:
      BadImplementationException e = thrown()
      e.message == 'cleanerService.clean.text.null'
    where:
      text = ''
      income = false

  }

}
