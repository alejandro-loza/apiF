package mx.finerio.api.services

import groovy.json.JsonSlurper

import mx.finerio.api.dtos.ApiListDto
import mx.finerio.api.dtos.CredentialErrorDto

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient
import java.time.ZonedDateTime
import mx.finerio.api.exceptions.BadImplementationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
class CallbackGatewayClientService {

  @Value( '${callback.gateway.url}' )
  String callbackGatewayUrl

  @Value( '${callback.gateway.registerCredential.path}' )
  String registerCredentialPath


  final static Logger log = LoggerFactory.getLogger(
    'mx.finerio.api.services.CallbackGatewayClientService' )
 
  String registerCredential( Map data ) throws Exception {
    
    def client = new RESTClient( callbackGatewayUrl )
    def response
    try{            
       response = client.post( path: registerCredentialPath ) {
        json data
      }
    }catch( wslite.rest.RESTClientException e ){
      log.info( "XX ${e.class.simpleName} - ${e.message}" )
      throw new BadImplementationException(
       'callbackGatewayClientService.registerCredential.error.registeringCredential')
    }
   
    response.statusMessage
  }
  
}
