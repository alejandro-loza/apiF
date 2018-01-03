package mx.finerio.api.services

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class DevScraperService {

  private static final MediaType JSON =
      MediaType.parse( 'application/json; charset=utf-8' )

  @Value( '${scraper.url}' )
  String url

  @Value( '${scraper.login.path}' )
  String loginPath

  @Value( '${scraper.login.credentials}' )
  String loginCredentials

  @Value( '${scraper.credentials.path}' )
  String credentialsPath

  @Async
  Map requestData( Map data ) throws Exception {
    post( credentialsPath, [ data: [ data ] ] )
  }

  private Map post( String path, Map data ) throws Exception {

    def token = login().authorizationToken
    def client = getClient()
    def json = new JsonBuilder( data ).toString()
    def body = RequestBody.create( JSON, json )
    def request = new Request.Builder()
      .url( "${url}/${path}" )
      .addHeader( 'Authorization', "Bearer ${token}" )
      .post( body )
      .build()
    def response = client.newCall( request ).execute()
    def responseBody = response.body().string()
    new JsonSlurper().parseText( responseBody ?: '{}' )

  }

  private Map login() throws Exception {

    def client = getClient()
    def request = new Request.Builder()
      .url( "${url}/${loginPath}" )
      .get()
      .addHeader( 'Authorization', "Basic ${loginCredentials}" )
      .build()
    def response = client.newCall( request ).execute()
    def responseBody = response.body()
    new JsonSlurper().parseText( responseBody.string() )

  }

  private OkHttpClient getClient() throws Exception {

    new OkHttpClient().newBuilder()
      .hostnameVerifier( new HostnameVerifier() {
        boolean verify( String hostname, SSLSession session ) {
          true
        }
      }).build()

  }

}
