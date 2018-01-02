package mx.finerio.api.services

import spock.lang.Specification

class DevScraperServicePostSpec extends Specification {

  def service = new DevScraperService()

  def setup() {

    service.url = 'CHANGEME'
    service.loginPath = 'CHANGEME'
    service.loginCredentials = 'CHANGEME'
    service.credentialsPath = 'CHANGEME'

  }

  @spock.lang.Ignore
  def "invoking method successfully"() {

    when:
      def result = service.post( path, data )
    then:
      result instanceof Map
    where:
      path = 'api/services/credentials'
      data = getData()

  }

  private Map getData() throws Exception {

    [
      data: [
        id: 'MyId',
        username: 'myUsername',
        password: 'myPassword',
        iv: 'myIv',
        user: [
          id: 'myUserId'
        ],
        institution: [
          id: 'myInstitutionId'
        ],
       securityCode: ''
      ]
    ] 

  }

}
