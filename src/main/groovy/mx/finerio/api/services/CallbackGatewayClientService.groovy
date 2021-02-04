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

@Service
class CallbackGatewayClientService {

  @Value( '${callback.gateway.url}' )
  String callbackGatewayUrl

  @Value( '${callback.gateway.registerCredential.path}' )
  String registerCredentialPath
 
  String registerCredential( Map data ) throws Exception {
    
    def client = new RESTClient( callbackGatewayUrl )
    try{            
      def response = client.post( path: registerCredentialPath ) {
        json data
      }
    }catch( wslite.rest.RESTClientException ex ){
      throw new BadImplementationException(
       'callbackGatewayClientService.registerCredential.error.registeringCredential')
    }
   
    response.statusMessage
  }
  
}
