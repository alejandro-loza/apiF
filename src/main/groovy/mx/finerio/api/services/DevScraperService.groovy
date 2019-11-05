package mx.finerio.api.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.MediaType
import okhttp3.RequestBody
import groovy.json.*

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
    def headers = [ 'Authorization': "Bearer ${login().access_token}" ]
    def body = [ data: [ data ] ]
    restTemplateService.post( finalUrl, headers, body )

  }

 private Map login() throws Exception{
    
    OkHttpClient client = new OkHttpClient()
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
    RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&scope=all")
    Request request = new Request.Builder()
      .url("${url}/${loginPath}")
      .post(body)
      .addHeader("Content-Type", "application/x-www-form-urlencoded")
      .addHeader("Authorization", "Basic ${loginCredentials}")
      .build()

    Response response = client.newCall(request).execute()
    def responseString = response.body().string()
    def statusCode = response.code()
    if( statusCode != 200 ){ throw new Exception( responseString ) }   
    def res = new JsonSlurper().parseText( responseString ?: '{}' )    
    res
  }

}
