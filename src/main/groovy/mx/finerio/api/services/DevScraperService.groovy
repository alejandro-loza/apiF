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
import mx.finerio.api.domain.Credential
import mx.finerio.api.dtos.*

@Service
class DevScraperService {

  private static final int MAX_MILLISECONDS_TO_REFRESH = 5000

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

  @Autowired
  CredentialFailureService credentialFailureService

  String accessToken
  Integer expiresIn
  Long lastTokenFetchingTime

  Map requestData( Credential credential ) throws Exception {

    def data = [
      id: credential.id,
      username: credential.username,
      password: credential.password,
      iv: credential.iv,
      user: [ id: credential.user.id ],
      institution: [ id: credential.institution.id ],
      securityCode: credential.securityCode
    ]

    requestData( data )

  }

  @Async
  Map requestData( Map data ) throws Exception {

    if ( this.accessToken == null ) { login() }

    try{

      def finalUrl = "${url}/${credentialsPath}"
      def headers = [ 'Authorization': "Bearer ${getCurrentAccessToken()}" ]
      def body = [ data: [ data ] ]
      restTemplateService.post( finalUrl, headers, body ) 

    }catch( java.net.SocketTimeoutException | Exception ex ){  
      credentialFailureService.processFailure( getTimeoutFailureCallbackDto( data.id ) )
      throw new Exception( ex.message )
    }

  }

    private getTimeoutFailureCallbackDto( String credentialId ){
      String errorMessage = 'Hubo un problema de conexión con tu banco. Sincroniza tu cuenta nuevamente en 5 minutos.'
      Integer statusCode = 504  
      def data =new FailureCallbackData( credential_id: credentialId, error_message: errorMessage, status_code: statusCode )
      new FailureCallbackDto( data: data)
  }

 private void login() throws Exception{
    
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
    this.accessToken = res.access_token as String
    this.expiresIn = res.expires_in as Integer
    this.lastTokenFetchingTime = new Date().time

  }

  private String getCurrentAccessToken() {

    def now = new Date().time
    def tokenLimitTime = this.lastTokenFetchingTime +
        ( this.expiresIn * 1000 ) - MAX_MILLISECONDS_TO_REFRESH

    if ( tokenLimitTime < now ) {
      login()
    }

    return this.accessToken

  }

}
