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

  @Value( '${satws.links.path}' )
  String linksPath

  @Value( '${satws.invoice.path}' )
  String invoicePath

  @Value( '${satws.payments.path}' )
  String paymentsPath

  @Value( '${satws.invoicePayments.path}' )
  String invoicePaymentsPath

  @Value( '${satws.taxpayerInvoicePayments.path}' )
  String taxpayerInvoicePaymentsPath
  
  @Value( '${satws.apikey.path}' )
  String satwsApikey

  def satwsClient

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
  
  Map getInvoicesByParams( String rfc, Map params ) throws Exception {
    
     if ( !rfc ) {
      throw new BadImplementationException(
        'satwsClientService.getInvoicesByParams.rfc.null' )
    }  

    def response
    String updatedPath = invoicesPath.replace( '{rfc}', rfc )
      
    try{ 

      response = satwsClient.get( 
        path: updatedPath, 
        query: params,
        headers: [ 'X-API-Key': satwsApikey ] ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )    
        throw new BadImplementationException(
          'satwsClientService.getInvoicesByParams.error.onCall')      
    }

    new JsonSlurper().parseText( new String( response.data, UTF_8) )

  }

  String getInvoice( String invoiceId, String accept ) throws Exception {

    
    def response
    def updatedPath = invoicePath.replace( '{invoiceId}', invoiceId )
    def headers = [ 'X-API-Key': satwsApikey, 'Accept': accept ]    
      
    try{ 

      response = satwsClient.get( path: updatedPath,         
        headers: headers ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" ) 
      return  new String( e.response.data, UTF_8)    
        
    }

    new String( response.data, UTF_8)        
  }

    Map getLinksByParams( String rfc, Map params ) throws Exception {
    
     if ( !rfc ) {
      throw new BadImplementationException(
        'satwsClientService.getLinksByParams.rfc.null' )
    }  

    def response
        
    try{ 

      response = satwsClient.get( 
        path: linksPath, 
        query: params,
        headers: [ 'X-API-Key': satwsApikey ] ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )    
        throw new BadImplementationException(
          'satwsClientService.getLinksByParams.error.onCall')      
    }

    new JsonSlurper().parseText( new String( response.data, UTF_8) )

  }

  Map getLink( String linkId  ) throws Exception {
    
    def response
    def updatedPath = "$linksPath/$linkId"
    def headers = [ 'X-API-Key': satwsApikey ]    
      
    try{ 

      response = satwsClient.get( path: updatedPath,         
        headers: headers ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )    
      throw new BadImplementationException(
      'satwsClientService.getLink.error.onCall')    
        
    }
    new JsonSlurper().parseText( new String( response.data, UTF_8) )
  }


  String deleteLink( String linkId ) throws Exception {

    
    def response
    def updatedPath = "$linksPath/$linkId"
    def headers = [ 'X-API-Key': satwsApikey ]    
      
    try{ 

      response = satwsClient.delete( path: updatedPath,         
        headers: headers ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" ) 
      return  new String( e.response.data, UTF_8)    
        
    }
    response.statusMessage
  }
///start payments

Map getPayments( Map params ) throws Exception {
   
    def response
        
    try{ 

      response = satwsClient.get( 
        path: paymentsPath, 
        query: params,
        headers: [ 'X-API-Key': satwsApikey ] ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )    
        throw new BadImplementationException(
          'satwsClientService.getPayments.error.onCall')      
    }

    new JsonSlurper().parseText( new String( response.data, UTF_8) )

  }

  String getPayment( String paymentId  ) throws Exception {

    
    def response
    def updatedPath = "$paymentsPath/$paymentId"
    def headers = [ 'X-API-Key': satwsApikey ]    
      
    try{ 

      response = satwsClient.get( path: updatedPath,         
        headers: headers ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" ) 
      return  new String( e.response.data, UTF_8)    
        
    }
    new JsonSlurper().parseText( new String( response.data, UTF_8) )
  }

Map getInvoicePayments( String invoiceId , Map params ) throws Exception {
   
    def response
    String updatedPath = invoicePaymentsPath.replace( '{invoiceId}', invoiceId )
        
    try{ 

      response = satwsClient.get( 
        path: updatedPath, 
        query: params,
        headers: [ 'X-API-Key': satwsApikey ] ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )    
        throw new BadImplementationException(
          'satwsClientService.getInvoicePayments.error.onCall')      
    }

    new JsonSlurper().parseText( new String( response.data, UTF_8) )

  }


  Map getTaxpayerInvoicePayments( String taxPayerId , Map params ) throws Exception {
   
    def response
    String updatedPath = taxpayerInvoicePaymentsPath.replace( '{taxPayerId}', taxPayerId )
        
    try{ 

      response = satwsClient.get( 
        path: updatedPath, 
        query: params,
        headers: [ 'X-API-Key': satwsApikey ] ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )    
        throw new BadImplementationException(
          'satwsClientService.getTaxpayerInvoicePayments.error.onCall')      
    }

    new JsonSlurper().parseText( new String( response.data, UTF_8) )

  }





//Finish payments






  @Override
  public void afterPropertiesSet() throws Exception {
      satwsClient = new RESTClient( url )    

      
  } 

}
