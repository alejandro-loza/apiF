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
import mx.finerio.api.dtos.email.EmailFromDto
import mx.finerio.api.dtos.email.EmailSendDto
import mx.finerio.api.dtos.email.EmailTemplateDto
import org.springframework.scheduling.annotation.Async

@Service
class SatwsService {

  final static String DEFAULT_TYPE = "ciec"
  final static Integer WRONG_CREDENTIAL_CODE = 401
  final static String WRONG_CREDENTIAL_MESSAGE = "Tu usuario o contraseña son incorrectos"
  final static String SATWS_CODE = "SAT"
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

  @Value('${sat.success.template}')
  String templateName
  
  @Value('${sat.success.from.email}')
  String satFromEmail

  @Value('${sat.success.from.name}')
  String satFromName

  @Autowired
  EmailRestService emailRestService


  String createCredential( CreateCredentialSatwsDto dto ) throws Exception {
        
    dto.type = DEFAULT_TYPE      
    def credentialProviderId = satwsClientService.createCredential( dto )    
    credentialProviderId

  }

 
  private void createExtractions( SatwsEventDto dto ){


    String rfc = 
        dto?.data?.object?.credential?.rfc
    String credentialId = 
        dto?.data?.object?.credential?.metadata?.credentialId

    def taxpayer="/taxpayers/$rfc"

    EXTRACTORS.each{
      def createExtractionDto = new CreateExtractionDto( taxpayer: taxpayer, 
        extractor: it, credentialId: credentialId )
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
            createExtractions( dto )
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

  }

  private void processExtrationUpdatedEvent( SatwsEventDto dto ){

    def credentialId=dto?.data?.object?.metadata?.credentialId
    def credential = credentialService.findAndValidate( credentialId )
    def rfc = credential.username
    def extractions = getExtractions( ['taxpayer.id' :rfc ])
    def finished = extractions['hydra:member'].every{ it.status == 'finished' }

    if( finished ){
      def email = credential.customer.client.email
      if(email){
        sendEmail(credential)
      }      
    }
    
  }

  private void sendEmail( Credential credential ){

    def customer = credential.customer
    def dto = new EmailSendDto(
      from: new EmailFromDto(
        email: satFromEmail,
        name: satFromName
      ),
      to: [ customer.client.email ],
      template: new EmailTemplateDto(
        name: templateName,
        params: [
          customerName: customer.name,
          customerRfc: credential.username
        ]
      )
    )
    emailRestService.send( dto )
  }

  private void processSuccess( SatwsEventDto dto ) throws Exception {

    def credentialId = dto?.data?.object?.credential.metadata?.credentialId
    def credentialProviderId = dto?.data?.object?.id

    credentialService.updateProviderId( credentialId, credentialProviderId )
       
    def successdto = SuccessCallbackDto
      .getInstanceFromCredentialId( credentialId )
      
    def credential = scraperCallbackService.processSuccess( successdto )
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
    def resParams = assignParametersInvoice(params)
    
    def result = satwsClientService.getInvoicesByParams( rfc, resParams )
    
    def data = result['hydra:member'].collect{
      [
       date: it.issuedAt,
       issuer: it.issuer.rfc,
       receiver: it.receiver.rfc,
       total: it.total,
       status: it.status,
       uuid: it.id
      ]}

    def finalRes = [ data: data ]
    def view = result['hydra:view']
    if ( view ){
      def next = view['hydra:next']
      if( next ){
        finalRes['nextCursor'] = next.reverse().take(1)
      }  
    }
    

    finalRes 
  }

  private Map assignParametersInvoice( Map params ) {
    def resMap
    params.customerId = null
    resMap = params.findAll { it.key != 'customerId'}

    if( params.cursor ){ 
      resMap.page = params.cursor
      resMap = resMap.findAll { it.key != 'cursor'}
    }
    if( params.maxResults ){      
      resMap.itemsPerPage = params.maxResults
      resMap = resMap.findAll { it.key != 'maxResults'}
    }

    resMap    
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
