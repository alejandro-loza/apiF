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
class CallbackGatewayClientService {

  @Value( '${callback.gateway.url}' )
  String callbackGatewayUrl

  @Value( '${callback.gateway.registerCredential.path}' )
  String registerCredentialPath
 
  //TODO handle when services return error
  String registerCredential( Map data ) throws Exception {
    
    def client = new RESTClient( callbackGatewayUrl )            
    def response = client.post( path: registerCredentialPath ) {
      json data
    }
   
    response.statusMessage
  }
  
}
