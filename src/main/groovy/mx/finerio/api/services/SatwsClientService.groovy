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

  @Value( '${satws.apikey.path}' )
  String satwsApikey

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

  @Value( '${satws.batchPayment.path}' )
  String batchPaymentsPath

  @Value( '${satws.invoiceBatchPayments.path}' )
  String invoiceBatchPaymentsPath

  @Value( '${satws.taxpayersTaxReturns.path}' )
  String taxpayersTaxReturnsPath

  @Value( '${satws.taxReturns.path}' )
  String taxReturnsPath

  @Value( '${satws.taxReturnsData.path}' )
  String taxReturnsDataPath

  @Value( '${satws.taxpayersTaxComplianceChecks.path}' )
  String taxpayersTaxComplianceChecksPath

  @Value( '${satws.taxComplianceChecks.path}' )
  String taxComplianceChecksPath

  @Value( '${satws.extractions.path}' )
  String extractionsPath
  
  def satwsClient

  final static Logger log = LoggerFactory.getLogger(
    'mx.finerio.api.services.SatwsClientService' )


  //Starts Credentials
  
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


  //Ends Credentials

  //starts Invoices  
  
  Map getInvoicesByParams( String rfc, Map params ) throws Exception {
    
    if ( !rfc ) {
      throw new BadImplementationException(
        'satwsClientService.getInvoicesByParams.rfc.null' )
    }  

    getDataByIdAndParams( rfc,'rfc',params, invoicesPath )  
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

  //Ends Invoices

  //Starts Links

  Map getLinksByParams( String rfc, Map params ) throws Exception {
    
    if ( !rfc ) {
      throw new BadImplementationException(
        'satwsClientService.getLinksByParams.rfc.null' )
    }  

    getDataByIdAndParams( rfc,'rfc', params, linksPath )  

  }

  Map getLink( String linkId  ) throws Exception{

    if ( !linkId ) {
      throw new BadImplementationException(
        'satwsClientService.getLink.linkId.null' )
    }

    getDataById( linkId, linksPath )  
    
  }
  
  String deleteLink( String linkId ) throws Exception {

    if ( !linkId ) {
      throw new BadImplementationException(
        'satwsClientService.getLink.linkId.null' )
    }

    deleteDataById( linkId, linksPath )  
    
  }

  //Ends Links
  // Starts Payments
  Map getPayments( Map params ) throws Exception {
    getDataByParams( params, paymentsPath )
  }


  String getPayment( String paymentId  ) throws Exception {

    if ( !paymentId ) {
      throw new BadImplementationException(
        'satwsClientService.getPayment.paymentId.null' )
    }

    getDataById( paymentId, paymentsPath )  
    
  }

  Map getInvoicePayments( String invoiceId , Map params ) throws Exception {

    if ( !invoiceId ) {
      throw new BadImplementationException(
        'satwsClientService.getInvoicePayments.invoiceId.null' )
    }  

    getDataByIdAndParams( invoiceId,'invoiceId',params, invoicePaymentsPath )  
   
  }


  Map getTaxpayerInvoicePayments( String taxPayerId , Map params ) throws Exception {

     if ( !taxPayerId ) {
      throw new BadImplementationException(
        'satwsClientService.getTaxpayerInvoicePayments.taxPayerId.null' )
    }  

    getDataByIdAndParams( taxPayerId,'taxPayerId', params, taxpayerInvoicePaymentsPath )  

  }
  // Ends Payments
 //Start batch Payments

  Map getBatchPayments( Map params ) throws Exception {
    getDataByParams( params, batchPaymentsPath )       
  }

  String getBatchPayment( String batchPaymentId  ) throws Exception {
    
    if ( !batchPaymentId ) {
      throw new BadImplementationException(
        'satwsClientService.getBatchPayment.batchPaymentId.null' )
    }

    getDataById( batchPaymentId, batchPaymentsPath )  
  }


  Map getInvoiceBatchPayments( String invoiceId, Map params ) throws Exception {
    
    if ( !invoiceId ) {
      throw new BadImplementationException(
        'satwsClientService.getInvoiceBatchPayments.invoiceId.null' )
    }  

    getDataByIdAndParams( invoiceId,'invoiceId', params, invoiceBatchPaymentsPath )

  }

  //Ends batch Payments

  //Starts tax returns

  Map getTaxpayersTaxReturns( String taxPayerId, Map params ) throws Exception {
    
    if ( !taxPayerId ) {
      throw new BadImplementationException(
        'satwsClientService.getTaxpayersTaxReturns.taxPayerId.null' )
    }  

    getDataByIdAndParams( taxPayerId,'taxPayerId', params, taxpayersTaxReturnsPath )

  }

  String getTaxReturn( String taxReturnId  ) throws Exception {
    
    if ( !taxReturnId ) {
      throw new BadImplementationException(
        'satwsClientService.getTaxReturn.taxReturnId.null' )
    }

    getDataById( taxReturnId, taxReturnsPath )  
  }


  Map getTaxReturnData( String taxReturnId ) throws Exception {
    
    if ( !taxReturnId ) {
      throw new BadImplementationException(
        'satwsClientService.getTaxReturnData.taxReturnId.null' )
    }  

    getDataByIdAndParams( taxReturnId,'taxReturnId', [:], taxReturnsDataPath )

  }


  String deleteTaxReturn( String taxReturnId ) throws Exception {

    if ( !taxReturnId ) {
      throw new BadImplementationException(
        'satwsClientService.deleteTaxReturn.taxReturnId.null' )
    }

    deleteDataById( taxReturnId, taxReturnsPath )  
    
  }

  //Ends tax returns


  //Start tax compliance check

  Map getTaxpayersTaxComplianceChecks( String taxPayerId, Map params ) throws Exception {
    
    if ( !taxPayerId ) {
      throw new BadImplementationException(
        'satwsClientService.getTaxpayersTaxComplianceChecks.taxPayerId.null' )
    }  

    getDataByIdAndParams( taxPayerId,'taxPayerId', params, taxpayersTaxComplianceChecksPath )

  }

  String getTaxComplianceCheck( String taxComplianceCheckId  ) throws Exception {
    
    if ( !taxComplianceCheckId ) {
      throw new BadImplementationException(
        'satwsClientService.getTaxComplianceCheck.taxComplianceCheckId.null' )
    }

    getDataById( taxComplianceCheckId, taxComplianceChecksPath )  
  }


  String deleteTaxComplianceCheck( String taxComplianceCheckId ) throws Exception {

    if ( !taxComplianceCheckId ) {
      throw new BadImplementationException(
        'satwsClientService.deleteTaxComplianceCheck.taxComplianceCheckId.null' )
    }

    deleteDataById( taxComplianceCheckId, taxComplianceChecksPath )  
    
  }

  //Ends tax compliance check

  //Starts extraction

  Map getExtractions( Map params ) throws Exception {
    getDataByParams( params, extractionsPath )       
  }

  String getExtraction( String extractionId  ) throws Exception {
    
    if ( !extractionId ) {
      throw new BadImplementationException(
        'satwsClientService.getExtraction.extractionId.null' )
    }

    getDataById( extractionId, extractionsPath )  
  }  

   //TODO create extraction

  //Ends extraction
  //Generics methods

  Map getDataByIdAndParams( String id, String change, Map params, String path ) throws Exception {

    def response  
    String updatedPath = path.replace( "{$change}", id )
      
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

  Map getDataByParams( Map params, String path ) throws Exception {
   
    def response
        
    try{ 

      response = satwsClient.get( 
        path: path, 
        query: params,
        headers: [ 'X-API-Key': satwsApikey ] ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )    
        throw new BadImplementationException(
          'satwsClientService.getDataByParams.error.onCall')      
    }

    new JsonSlurper().parseText( new String( response.data, UTF_8) )

  }


  Map getDataById( String id, String path  ) throws Exception {
    
    def response
    def updatedPath = "$path/$id"
    def headers = [ 'X-API-Key': satwsApikey ]    
      
    try{ 

      response = satwsClient.get( path: updatedPath,         
        headers: headers ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )    
      throw new BadImplementationException(
      "satwsClientService.getDataById.error.onCall")    
        
    }
    new JsonSlurper().parseText( new String( response.data, UTF_8) )
  }


   String deleteDataById ( String id, String path ) throws Exception {

    
    def response
    def updatedPath = "$path/$id"
    def headers = [ 'X-API-Key': satwsApikey ]    
      
    try{ 

      response = satwsClient.delete( path: updatedPath,         
        headers: headers ) 

    }catch( wslite.rest.RESTClientException e ){

      log.info( "XX ${e.class.simpleName} - ${e.message} ${new String( e.getResponse().data )}" )    
      throw new BadImplementationException(
      "satwsClientService.deleteDataById.error.onCall")     
        
    }

    response.statusMessage
  }

  @Override
  public void afterPropertiesSet() throws Exception {
      satwsClient = new RESTClient( url )          
  } 


}
