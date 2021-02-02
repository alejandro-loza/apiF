package mx.finerio.api.services

import groovy.json.JsonSlurper

import mx.finerio.api.dtos.ApiListDto
import mx.finerio.api.dtos.CredentialErrorDto

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient
import java.time.ZonedDateTime

@Service
class ScraperV2ClientService {

  @Value( '${scraper.v2.login.url}' )
  String loginUrl

  @Value( '${scraper.v2.login.path}' )
  String loginPath

  @Value( '${scraper.v2.login.clientId}' )
  String loginClientId

  @Value( '${scraper.v2.login.clientSecret}' )
  String loginClientSecret

  @Value( '${scraper.v2.url}' )
  String scraperV2Url

  @Value( '${scraper.v2.errors.path}' )
  String scraperV2ErrorsPath

  @Value( '${scraper.v2.publicKey.path}' )
  String scraperV2PublicKeyPath

  @Value( '${scraper.v2.credential.path}' )
  String scraperV2CredentialPath

  String token 
  ZonedDateTime tokenTime = ZonedDateTime.now().minusMinutes( 60 )
  Integer tokenMinutesDuration = 60

  private String getAccessToken() throws Exception {

    def minusOneHour = ZonedDateTime.now().minusMinutes( tokenMinutesDuration )            
    if( minusOneHour.isBefore( tokenTime ) ) 
    { return this.token }

    def client = new RESTClient( loginUrl )
    client.authorization = new HTTPBasicAuthorization( loginClientId,
        loginClientSecret )

    def response = client.post( path: loginPath ) {
      urlenc grant_type: 'client_credentials'
    }

    def jsonMap = new JsonSlurper().parseText( new String( response.data ) )
    
    this.token = jsonMap.access_token
    this.tokenTime = ZonedDateTime.now()      
    this.token

  }

  List getErrors() throws Exception {
    
    def client = new RESTClient( scraperV2Url )
    def headers = [ 'Authorization': "Bearer ${getAccessToken()}" ]
    def response = client.get( path: scraperV2ErrorsPath, headers: headers )
    def jsonMap = new JsonSlurper().parseText( new String( response.data ) )
    jsonMap.data

  }

  Map getPublicKey() throws Exception {
    
    def client = new RESTClient( scraperV2Url )
    def headers = [ 'Authorization': "Bearer ${getAccessToken()}" ]
    def response = client.get( path: scraperV2PublicKeyPath, headers: headers )
    new JsonSlurper().parseText( new String( response.data ) )

  }  

  String createCredential( Map data ) throws Exception {
    
    def client = new RESTClient( scraperV2Url )
    def headers = [ 'Authorization': "Bearer ${getAccessToken()}" ]
        def response = client.post( path: scraperV2CredentialPath ) {
      json data
    }
   
    response.statusMessage    
  }  



}
