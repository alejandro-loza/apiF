package mx.finerio.api.services

import spock.lang.Specification

class DevScraperServiceLoginSpec extends Specification {

  def service = new DevScraperService()

  def setup() {
/*
    service.url = 'CHANGEME'
    service.loginPath = 'CHANGEME'
    service.loginCredentials = 'CHANGEME'
*/
    service.url = 'https://finerio-dev.southcentralus.cloudapp.azure.com'
    service.loginPath = 'api/login'
    service.loginCredentials = 'c2NyYXBlcjpzY3JhcGVyXzIzNDUk'

  }

  def "invoking method successfully"() {

    when:
      def result = service.login()
    then:
      result instanceof Map
      result.authorizationToken != null
      result.expires != null

  }

}
