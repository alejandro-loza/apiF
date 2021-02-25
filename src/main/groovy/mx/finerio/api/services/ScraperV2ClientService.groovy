package mx.finerio.api.services

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import mx.finerio.api.dtos.ApiListDto
import mx.finerio.api.dtos.CredentialErrorDto

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient
import java.time.ZonedDateTime
import org.springframework.beans.factory.InitializingBean
import mx.finerio.api.exceptions.BadImplementationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
class ScraperV2ClientService  implements InitializingBean {

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

  @Value( '${scraper.v2.credential.legacy.path}' )
  String scraperV2CredentialLegacyPath

  @Value( '${scraper.v2.interactive.path}' )
  String scraperV2InteractivePath

  String token 
  ZonedDateTime tokenTime = ZonedDateTime.now().minusMinutes( 60 )
  Integer tokenMinutesDuration = 60

  def loginClient
  def scraperClient

  final static Logger log = LoggerFactory.getLogger(
    'mx.finerio.api.services.ScraperV2ClientService' )

  private String getAccessToken() throws Exception {

    def minusOneHour = ZonedDateTime.now().minusMinutes( tokenMinutesDuration )            
    if( minusOneHour.isBefore( tokenTime ) ) 
    { return this.token }
    
    loginClient.authorization = new HTTPBasicAuthorization( loginClientId,
        loginClientSecret )

    def response 

    try{

      response = loginClient.post( path: loginPath ) {
        urlenc grant_type: 'client_credentials'
      }

    }catch( wslite.rest.RESTClientException e ){
      log.info( "XX ${e.class.simpleName} - ${e.message}" )
      throw new BadImplementationException(
       'scraperV2ClientService.getAccessToken.error.onCall')
    }

    def jsonMap = new JsonSlurper().parseText( new String( response.data ) )
    
    this.token = jsonMap.access_token
    this.tokenTime = ZonedDateTime.now()      
    this.token

  }

  List getErrors() throws Exception {
    
    def response   
    
    try{ 

      response = scraperClient.get( path: scraperV2ErrorsPath,
        headers: [ 'Authorization': "Bearer ${getAccessToken()}" ] )
    
    }catch( wslite.rest.RESTClientException e ){
      log.info( "XX ${e.class.simpleName} - ${e.message}" )
      throw new BadImplementationException(
       'scraperV2ClientService.getErrors.error.onCall')
    }

    def jsonMap = new JsonSlurper().parseText( new String( response.data ) )
    jsonMap.data

  }

  Map getPublicKey() throws Exception {
    
    def response

    try{       

      response = scraperClient.get( path: scraperV2PublicKeyPath,
        headers: [ 'Authorization': "Bearer ${getAccessToken()}" ] )

    }catch( wslite.rest.RESTClientException e ){
      log.info( "XX ${e.class.simpleName} - ${e.message}" )
      throw new BadImplementationException(
       'scraperV2ClientService.getPublicKey.error.onCall')
    }
    
    new JsonSlurper().parseText( new String( response.data ) )

  }  
  

  String createCredential( Map data ) throws Exception {

    validateInputCreateCredential( data )
      
    def response
      
    try{ 

      response = scraperClient.post( path: scraperV2CredentialPath,
        headers: [ 'Authorization': "Bearer ${getAccessToken()}" ] ) {
          json data
        }

    }catch( wslite.rest.RESTClientException e ){
      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )
      throw new BadImplementationException(
          'scraperV2ClientService.createCredential.error.onCall')
    }

    response.statusMessage    
  }



   String createCredentialLegacy( Map data) throws Exception {

    validateInputCreateCredentialLegacy( data )
    
    def response
      
    try{ 

      response = scraperClient.post( path: scraperV2CredentialLegacyPath,
        headers: [ 'Authorization': "Bearer ${getAccessToken()}" ] ) {
          json data
        }

    }catch( wslite.rest.RESTClientException e ){
      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )
      throw new BadImplementationException(
          'scraperV2ClientService.createCredential.error.onCall')
    }
    
    response.statusMessage    
  }


  private void validateInputCreateCredential( Map data ){
    
    if( !data.containsKey('institution') ){
     throw new BadImplementationException(
        'scraperV2ClientService.validateInputCreateCredential.institution.null')
    }

    if( !data.containsKey('data') ){
     throw new BadImplementationException(
        'scraperV2ClientService.validateInputCreateCredential.data.null')
    }

    if( !data.containsKey('state') ){
     throw new BadImplementationException(
        'scraperV2ClientService.validateInputCreateCredential.state.null')
    }

  }  


   private void validateInputCreateCredentialLegacy( Map data ){
    
    if( !data.containsKey('data') ){
     throw new BadImplementationException(
        'scraperV2ClientService.validateInputCreateCredential.data.null')
    }

  } 


  String sendInteractive( Map data ) throws Exception {
            
    def response

    try{

      response = scraperClient.post( path: scraperV2InteractivePath,
        headers: [ 'Authorization': "Bearer ${getAccessToken()}" ]  ) {
          json data
        }

    }catch( wslite.rest.RESTClientException e ){
      log.info( "XX ${e.class.simpleName} - ${e.message}" )
      throw new BadImplementationException(
          'scraperV2ClientService.sendInteractive.error.onCall')
    }
   
    response.statusMessage    
  }

    private void validateInputSendInteractive( Map data ){
    
    if( !data.containsKey('state') ){
     throw new BadImplementationException(
        'scraperV2ClientService.validateInputSendInteractive.state.null')
    }

    if( !data.containsKey('source') ){
     throw new BadImplementationException(
        'scraperV2ClientService.validateInputSendInteractive.source.null')
    }
    
  } 

  @Override
  public void afterPropertiesSet() throws Exception {
      loginClient = new RESTClient( loginUrl )
      scraperClient = new RESTClient( scraperV2Url )
  } 


}
