package mx.finerio.api.services

import spock.lang.Specification

class DevScraperServiceLoginSpec extends Specification {

  def service = new DevScraperService()

  def setup() {

    service.url = 'CHANGEME'
    service.loginPath = 'CHANGEME'
    service.loginCredentials = 'CHANGEME'

  }

  @spock.lang.Ignore
  def "invoking method successfully"() {

    when:
      def result = service.login()
    then:
      result instanceof Map
      result.authorizationToken != null
      result.expires != null

  }

}
