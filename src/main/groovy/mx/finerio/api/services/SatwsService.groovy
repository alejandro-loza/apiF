package mx.finerio.api.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.beans.factory.InitializingBean
import mx.finerio.api.exceptions.BadImplementationException
import org.springframework.beans.factory.annotation.Autowired
import mx.finerio.api.dtos.CreateCredentialSatwsDto
import mx.finerio.api.dtos.SatwsEventDto
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.dtos.FailureCallbackData
import mx.finerio.api.dtos.SuccessCallbackDto
import mx.finerio.api.domain.Credential
import mx.finerio.api.dtos.CreateExtractionDto
import mx.finerio.api.dtos.WidgetEventsDto

@Service
class SatwsService {

  final static String DEFAULT_TYPE = "ciec"
  final static Integer WRONG_CREDENTIAL_CODE = 401
  final static String WRONG_CREDENTIAL_MESSAGE = "Tu usuario o contraseña son incorrectos"
  final static String SATWS_CODE = "SATWS"
  final static String[] EXTRACTORS =  [ "invoice", "monthly_tax_return", "annual_tax_return", 
                                        "rif_tax_return", "tax_status", "tax_retention", 
                                        "tax_compliance" ]

  @Autowired
  SatwsClientService satwsClientService

  @Autowired
  CredentialFailureService credentialFailureService

  @Autowired
  CredentialService credentialService

  @Autowired
  CustomerService customerService

  @Autowired
  FinancialInstitutionService financialInstitutionService
  
  @Autowired
  ScraperCallbackService scraperCallbackService

  @Autowired
  WidgetEventsService widgetEventsService

  @Autowired
  MessageService messageService


  String createCredential( CreateCredentialSatwsDto dto ) throws Exception {
        
    dto.type = DEFAULT_TYPE      
    def credentialProviderId = satwsClientService.createCredential( dto )
    createExtractions(dto)
    credentialProviderId

  }

  private void createExtractions( CreateCredentialSatwsDto dto ){

    def taxpayer="/taxpayers/${dto.rfc}"

    EXTRACTORS.each{
      def createExtractionDto = new CreateExtractionDto( taxpayer: taxpayer, 
        extractor: it, credentialId: dto.credentialId )
      createExtraction( createExtractionDto )
    }

  }

  

  String deleteCredential( String credentialId ) throws Exception {
    satwsClientService.deleteCredential(credentialId)
  }

  void processEvent( SatwsEventDto dto ) throws Exception {

    validateInputProcessEvent( dto )

    String type = dto.type

    switch(type) {
      case 'credential.updated':
        String status = dto.data.object.status        
        if( 'invalid' == status ){
          processFailure( dto )
        }      
      break

      case 'link.created':
        String status = dto.data.object.credential.status
        if( 'valid' == status ){
            processSuccess( dto )
          }      
      break

      case 'extraction.updated':              
            processExtrationUpdatedEvent( dto )                
      break

    
    }
  } 

  private void validateInputProcessEvent( SatwsEventDto dto ){

    if ( !dto.type ) {
      throw new BadImplementationException(
        'satwsService.processEvent.type.null' )
    }

   if ( !dto.data?.object?.id ) {
      throw new BadImplementationException(
        'satwsService.processEvent.credentialId.null' )
    }

   if ( !dto.data?.object?.status ) {
      throw new BadImplementationException(
        'satwsService.processEvent.status.null' )
    }

  }

  private void processExtrationUpdatedEvent( SatwsEventDto dto ){

    def credentialId=dto?.data?.object?.metadata?.credentialId
    def rfc = credentialService.findAndValidate( credentialId ).username

    def extractions = getExtractions(['taxpayer.id':rfc])

    def finished = extractions['hydra:member'].every{ it.status == 'finished' }

    if( finished ){
      println 'Sending email'
    }
    

  }

  private Credential getCredentialByProviderId( String  providerId ){

    def financialInstitution = financialInstitutionService.findOneByCode( SATWS_CODE )
    if ( !financialInstitution ) {
      throw new BadImplementationException(
        'satwsService.getCredentialByProviderId.financialInstitution.notFound' )
    } 

    def credential = credentialService
      .findByScrapperCredentialIdAndInstitution(
        providerId, financialInstitution)

      credential
  }

  private Credential getCredentialByUserName( String  username ){

    def financialInstitution = financialInstitutionService.findOneByCode( SATWS_CODE )
    if ( !financialInstitution ) {
      throw new BadImplementationException(
        'satwsService.getCredentialByUserName.financialInstitution.notFound' )
    } 

    def credential = credentialService
      .findByInstitutionAndUsername(
        financialInstitution, username)

     credential
  }

  

  private void processSuccess( SatwsEventDto dto ) throws Exception {

    def credentialId = dto?.data?.object?.credential.metadata?.credentialId
    def credentialProviderId = dto?.data?.object?.id

    credentialService.updateProviderId( credentialId, credentialProviderId )
       
    def successdto = SuccessCallbackDto
      .getInstanceFromCredentialId( credentialId )
      
    credential = scraperCallbackService.processSuccess( successdto )
    scraperCallbackService.postProcessSuccess( credential )
    
  }

  private void processFailure( SatwsEventDto dto ) throws Exception {
 
    def credentialId = dto?.data?.object?.metadata?.credentialId

   	def failureDto = new FailureCallbackDto( 
   		data: new FailureCallbackData(
   			 credential_id: credentialId,
   			 error_message: WRONG_CREDENTIAL_MESSAGE,
   			 status_code: WRONG_CREDENTIAL_CODE) )
    
    credentialFailureService.processFailure( failureDto )    
    
  }

  Map getInvoicesByParams( Map params ) throws Exception {

     if ( !params.customerId ) {
      throw new BadImplementationException(
        'satwsService.getInvoicesByParams.params.customerId.null' )
    }  
    
     def rfc = getRfcByCustomerId( params.customerId as Long)
     params.customerId = null
     satwsClientService.getInvoicesByParams( rfc, params )

  }

  private String getRfcByCustomerId( Long customerId ){

    def financialInstitution = financialInstitutionService.findOneByCode( SATWS_CODE )
    if ( !financialInstitution ) {
      throw new BadImplementationException(
        'satwsService.getRfcByCustomerId.financialInstitution.notFound' )
    }      
    
    def customer = customerService.findOne( customerId )
    if ( !customer ) {
      throw new BadImplementationException(
        'satwsService.getRfcByCustomerId.customer.notFound' )
    }

    def credential = credentialService.findByCustomerAndFinancialIntitution( customer, financialInstitution )
    if ( !credential ) {
      throw new BadImplementationException(
        'satwsService.getRfcByCustomerId.credential.notFound' )
    }      
     
    credential.username  
  }

  String getInvoice( String invoiceId, String accept ) throws Exception {


    if ( !invoiceId ) {
      throw new BadImplementationException(
        'satwsService.getInvoice.invoiceId.null' )
    }  

    if ( !accept ) {
      throw new BadImplementationException(
        'satwsService.getInvoice.accept.null' )
    }      
    
    satwsClientService.getInvoice( invoiceId, accept )
  }

  String deleteInvoice( String invoiceId ) throws Exception {

      if ( !invoiceId ) {
      throw new BadImplementationException(
        'satwsService.deleteInvoice.invoiceId.null' )
    }  
    satwsClientService.deleteInvoice( invoiceId )
  }


  Map getLinksByParams( Map params ) throws Exception {

    if ( !params.customerId ) {
      throw new BadImplementationException(
        'satwsService.getLinksByParams.params.customerId.null' )
    }  
    
    def rfc = getRfcByCustomerId( params.customerId as Long )
    params.customerId = null
    satwsClientService.getLinksByParams( rfc, params )

  }

  Map getLink( String linkId  ) throws Exception {

    if ( !linkId ) {
      throw new BadImplementationException(
        'satwsService.getLink.linkId.null' )
    }      
    satwsClientService.getLink( linkId )

   }

  String deleteLink( String linkId ) throws Exception {
    
    if ( !linkId ) {
      throw new BadImplementationException(
        'satwsService.deleteLink.linkId.null' )
    }      
    satwsClientService.deleteLink( linkId )    
  }

  Map getPayments( Map params ) throws Exception {
    satwsClientService.getPayments( params )
  }


  Map getPayment( String paymentId  ) throws Exception {

    if ( !paymentId ) {
      throw new BadImplementationException(
        'satwsService.getPayment.paymentId.null' )
    }   
    satwsClientService.getPayment(paymentId)

  }

  Map getInvoicePayments( String invoiceId , Map params ) throws Exception {
   
   if ( !invoiceId ) {
      throw new BadImplementationException(
        'satwsService.getInvoicePayments.invoiceId.null' )
    }
    satwsClientService.getInvoicePayments( invoiceId, params )
  }
 
  Map getTaxpayerInvoicePayments( Long customerId , Map params ) throws Exception {
    if ( !customerId ) {
      throw new BadImplementationException(
        'satwsService.getTaxpayerInvoicePayments.customerId.null' )
    }
    def taxPayerId = getRfcByCustomerId( customerId )
    satwsClientService.getTaxpayerInvoicePayments( taxPayerId, params )
  }

  Map getBatchPayments( Map params ) throws Exception {
    satwsClientService.getBatchPayments( params )
  }

  Map getBatchPayment( String batchPaymentId  ) throws Exception {

    if ( !batchPaymentId ) {
      throw new BadImplementationException(
        'satwsService.getBatchPayment.batchPaymentId.null' )
    }   
    satwsClientService.getBatchPayment(batchPaymentId)

  }
  
  Map getInvoiceBatchPayments( String invoiceId, Map params ) throws Exception {
    
    if ( !invoiceId ) {
      throw new BadImplementationException(
        'satwsService.getInvoiceBatchPayments.invoiceId.null' )
    }
    satwsClientService.getInvoiceBatchPayments( invoiceId, params )
  }

  Map getTaxpayersTaxStatus( Long customerId, Map params ) throws Exception {
  
    if ( !customerId ) {
      throw new BadImplementationException(
        'satwsService.getTaxpayersTaxStatus.customerId.null' )
    }
    def taxPayerId = getRfcByCustomerId( customerId )
    satwsClientService.getTaxpayersTaxStatus( taxPayerId, params )
  }

  Map getTaxReturn( String taxReturnId  ) throws Exception {

      if ( !taxReturnId ) {
      throw new BadImplementationException(
        'satwsService.getTaxReturn.taxReturnId.null' )
    }   
    satwsClientService.getTaxReturn(taxReturnId)
  
  }

  Map getTaxReturnData( String taxReturnId ) throws Exception {

     if ( !taxReturnId ) {
      throw new BadImplementationException(
        'satwsService.getTaxReturnData.taxReturnId.null' )
    }   
    satwsClientService.getTaxReturnData( taxReturnId )
  
  }

  String deleteTaxReturn( String taxReturnId ) throws Exception {

     if ( !taxReturnId ) {
      throw new BadImplementationException(
        'satwsService.deleteTaxReturn.taxReturnId.null' )
    }      
    satwsClientService.deleteTaxReturn( taxReturnId )      
  
  }

  Map getTaxpayersTaxComplianceChecks( Long customerId, Map params ) throws Exception {

    if ( !customerId ) {
      throw new BadImplementationException(
        'satwsService.getTaxpayersTaxComplianceChecks.customerId.null' )
    }
    def taxPayerId = getRfcByCustomerId( customerId )
    satwsClientService.getTaxpayersTaxComplianceChecks( taxPayerId, params )

  }

  Map getTaxComplianceCheck( String taxComplianceCheckId  ) throws Exception {

    if ( !taxComplianceCheckId ) {
      throw new BadImplementationException(
        'satwsService.getTaxComplianceCheck.taxComplianceCheckId.null' )
    }   
    satwsClientService.getTaxComplianceCheck( taxComplianceCheckId )
  }

  String deleteTaxComplianceCheck( String taxComplianceCheckId ) throws Exception {
    
    if ( !taxComplianceCheckId ) {
      throw new BadImplementationException(
        'satwsService.deleteTaxComplianceCheck.taxComplianceCheckId.null' )
    }      
    satwsClientService.deleteTaxComplianceCheck( taxComplianceCheckId )      

  }

  String createExtraction( CreateExtractionDto dto ) throws Exception {
    satwsClientService.createExtraction( dto )
  }

  Map getExtractions( Map params ) throws Exception {
    satwsClientService.getExtractions( params )

  }

  Map getExtraction( String extractionId  ) throws Exception {

    if ( !extractionId ) {
      throw new BadImplementationException(
        'satwsService.getExtraction.extractionId.null' )
    }   
    satwsClientService.getExtraction( extractionId )

  }

  Map getTaxpayersTaxStatus( Long customerId ) throws Exception {
     
     if ( !customerId ) {
      throw new BadImplementationException(
        'satwsService.getTaxpayersTaxStatus.customerId.null' )
    }
    def taxPayerId = getRfcByCustomerId( customerId )
    satwsClientService.getTaxpayersTaxStatus( taxPayerId )
  }

  Map getTaxStatus( String taxStatusId  ) throws Exception {

     if ( !taxStatusId ) {
      throw new BadImplementationException(
        'satwsService.getTaxStatus.taxStatusId.null' )
    }   
    satwsClientService.getTaxStatus( taxStatusId )
  }

  String deleteTaxStatus( String taxStatusId ) throws Exception {

    if ( !taxStatusId ) {
      throw new BadImplementationException(
        'satwsService.deleteTaxStatus.taxStatusId.null' )
    }      
    satwsClientService.deleteTaxStatus( taxStatusId ) 

  }

  Map getTaxpayersTaxRetentions( Long customerId, Map params ) throws Exception {

    if ( !customerId ) {
      throw new BadImplementationException(
        'satwsService.getTaxpayersTaxRetentions.customerId.null' )
    }
    def taxPayerId = getRfcByCustomerId( customerId )
    satwsClientService.getTaxpayersTaxRetentions( taxPayerId, params )

  }

  Map getTaxRetention( String taxRetentionId  ) throws Exception {

     if ( !taxRetentionId ) {
      throw new BadImplementationException(
        'satwsService.getTaxRetention.taxRetentionId.null' )
    }   
    satwsClientService.getTaxRetention( taxRetentionId )
  }

  String deleteTaxRetention( String taxRetentionId ) throws Exception {

    if ( !taxRetentionId ) {
      throw new BadImplementationException(
        'satwsService.deleteTaxRetention.taxRetentionId.null' )
    }      
    satwsClientService.deleteTaxRetention( taxRetentionId ) 

  }

  String getTaxRetentionInvoice( String invoiceId, String accept ) throws Exception {


    if ( !invoiceId ) {
      throw new BadImplementationException(
        'satwsService.getTaxRetentionInvoice.invoiceId.null' )
    }  

    if ( !accept ) {
      throw new BadImplementationException(
        'satwsService.getTaxRetentionInvoice.accept.null' )
    }      
    
    satwsClientService.getTaxRetentionInvoice( invoiceId, accept )
  }  

}