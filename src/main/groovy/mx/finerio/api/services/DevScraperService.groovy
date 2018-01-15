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
  Okhttp3Service okhttp3Service

  @Async
  Map requestData( Map data ) throws Exception {
    post( credentialsPath, [ data: [ data ] ] )
  }

  private Map post( String path, Map data ) throws Exception {

    def token = login().authorizationToken
    Map map = [:]
    map.url= [ port: url+"/", service: path ]
    map.param = [ body: data ]
    map.auth = [ status: true, type: "Bearer", token: token ]
    def result = okhttp3Service.post(map)
    result	

  }

  private Map login() throws Exception {

    Map map = [:]
    map.url= [ port: url+"/", service: loginPath ]
    map.param = [ name: "", value: "" ]
    map.auth = [ status: true, type: "Basic", token: loginCredentials ]
    def result = okhttp3Service.get(map)
    result	
  }

}
