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
class SatwsClientService  implements InitializingBean {

  @Value( '${satws.url}' )
  String url

  @Value( '${satws.credential.path}' )
  String credentialPath

  @Value( '${satws.apikey.path}' )
  String satwsApikey

  def satwsClient

  final static Logger log = LoggerFactory.getLogger(
    'mx.finerio.api.services.SatwsClientService' )

  
   String createCredential( Map data) throws Exception {

    validateInputCreateCredential( data )
    
    def response
      
    try{ 

      response = satwsClient.post( path: credentialPath,
        headers: [ 'X-API-Key': satwsApikey ] ) {
          json data
        }

    }catch( wslite.rest.RESTClientException e ){
      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )
      throw new BadImplementationException(
          'satwsClientService.createCredential.error.onCall')
    }
    
    println response.statusMessage
    response.statusMessage
  }

//TODO validate valid RFC.
  private void validateInputCreateCredential( Map data ) throws Exception {
    
    if( !data.containsKey('type') ){
     throw new BadImplementationException(
        'satwsClientService.validateInputCreateCredential.type.null')
    }

    if( !data.containsKey('rfc') ){
     throw new BadImplementationException(
        'satwsClientService.validateInputCreateCredential.rfc.null')
    }

    if( !data.containsKey('password') ){
     throw new BadImplementationException(
        'satwsClientService.validateInputCreateCredential.password.null')
    }

  } 

  @Override
  public void afterPropertiesSet() throws Exception {
      satwsClient = new RESTClient( url )      
  } 

}
