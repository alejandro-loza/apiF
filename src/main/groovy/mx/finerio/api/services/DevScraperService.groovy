package mx.finerio.api.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class DevScraperService {

  @Value( '${scraper.url}' )
  String url

  @Value( '${scraper.login.path}' )
  String loginPath

  @Value( '${scraper.login.credentials}' )
  String loginCredentials

  @Value( '${scraper.credentials.path}' )
  String credentialsPath

  @Autowired
  RestTemplateService restTemplateService

  @Async
  Map requestData( Map data ) throws Exception {

    def finalUrl = "${url}/${credentialsPath}"
    def headers = [ 'Authorization': "Bearer ${login().authorizationToken}" ]
    def body = [ data: [ data ] ]
    restTemplateService.post( finalUrl, headers, body )

  }

  private Map login() throws Exception {

    def finalUrl = "${url}/${loginPath}"
    def headers = [ 'Authorization': "Basic ${loginCredentials}" ]
    def params = [:]
    restTemplateService.get( finalUrl, headers, params )

  }

}
