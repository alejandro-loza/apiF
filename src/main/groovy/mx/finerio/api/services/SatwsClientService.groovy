package mx.finerio.api.services

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import mx.finerio.api.dtos.ApiListDto
import mx.finerio.api.dtos.CreateCredentialSatwsDto

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient
import java.time.ZonedDateTime
import org.springframework.beans.factory.InitializingBean
import mx.finerio.api.exceptions.BadImplementationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import groovy.json.JsonSlurper
import static java.nio.charset.StandardCharsets.*

@Service
class SatwsClientService  implements InitializingBean {

  @Value( '${satws.url}' )
  String url

  @Value( '${satws.credential.path}' )
  String credentialPath

  @Value( '${satws.invoices.path}' )
  String invoicesPath

  @Value( '${satws.invoice.path}' )
  String invoicePath

  @Value( '${satws.apikey.path}' )
  String satwsApikey

  def satwsClient

  /*final static Map FILE_TYPES = 
      [ xml: 'text/xml', pdf: 'application/pdf', json: 'application/json' ]*/

  final static Logger log = LoggerFactory.getLogger(
    'mx.finerio.api.services.SatwsClientService' )

  
   String createCredential( CreateCredentialSatwsDto dto ) throws Exception {

    validateInputCreateCredential( dto )

    def data =  [ 'type': dto.type,
                  'rfc': dto.rfc ,
                  'password': dto.password  ]
    
    def response
      
    try{ 

      response = satwsClient.post( path: credentialPath,
        headers: [ 'X-API-Key': satwsApikey ] ) {
          json data
        }

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )

      if( e.response.statusCode == 400 ){
        def bodyResponse = new JsonSlurper().parseText( new String( e.response.data, UTF_8) )
          throw new BadImplementationException( bodyResponse['hydra:description'] )
      }else{
        throw new BadImplementationException(
          'satwsClientService.createCredential.error.onCall')
      }

    }

    def bodyResponse = new JsonSlurper().parseText( new String( response.data, UTF_8) )
    bodyResponse['id']  
  }

  
  Map getInvoicesByParams( String rfc, Map params ) throws Exception {
    //TODO validate input data
    def response
    def updatedPath = invoicesPath.replace( '{rfc}', rfc )​​​​​
      
    try{ 

      response = satwsClient.get( path: updatedPath, 
        query: params,
        headers: [ 'X-API-Key': satwsApikey ] ) {
          json data
        }

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )    
        throw new BadImplementationException(
          'satwsClientService.getInvoicesByParams.error.onCall')      
    }

    new JsonSlurper().parseText( new String( response.data, UTF_8) )
    
  }

  String getInvoice( String invoiceId, String accept ) throws Exception {
    //TODO validate input data
    def response
    def updatedPath = invoicePath.replace( '{invoiceId}', invoiceId )​​​​​
    def headers = [ 'X-API-Key': satwsApikey, 'Accept': accept ]    
      
    try{ 

      response = satwsClient.get( path: updatedPath, 
        query: params,
        headers: [ 'X-API-Key': satwsApikey ] ) {
          json data
        }

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )    
        throw new BadImplementationException(
          'satwsClientService.getInvoicesByParams.error.onCall')      
    }

    new String( response.data, UTF_8)    
  }


  private void validateInputCreateCredential( CreateCredentialSatwsDto data ) throws Exception {
    
    if( !data.type ){
     throw new BadImplementationException(
        'satwsClientService.validateInputCreateCredential.type.null')
    }

    if( !data.rfc ){
     throw new BadImplementationException(
        'satwsClientService.validateInputCreateCredential.rfc.null')
    }

    if( !data.password ){
     throw new BadImplementationException(
        'satwsClientService.validateInputCreateCredential.password.null')
    }

  } 

  @Override
  public void afterPropertiesSet() throws Exception {
      satwsClient = new RESTClient( url )      
  } 

}
