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


@Service
class SatwsService {

  final static String DEFAULT_TYPE = "ciec"
  final static Integer WRONG_CREDENTIAL_CODE = 401
  final static String WRONG_CREDENTIAL_MESSAGE = "Tu usuario o contraseña son incorrectos"
  final static String SATWS_CODE = "SATWS"
   
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


  String createCredential( CreateCredentialSatwsDto dto ) throws Exception {
        
    dto.type = DEFAULT_TYPE      
    satwsClientService.createCredential( dto )

  }

  void processEvent( SatwsEventDto dto ) throws Exception {

    validateInputProcessEvent( dto )

    if ( !'credential.updated'.equals(dto.type) ) {
      return
    }

    def status = dto.data.object.status

    switch( status ) {
    	case 'invalid':
    		processFailure( dto )
    	break
    }   

  }

  private void validateInputProcessEvent( SatwsEventDto dto ){

    if ( !dto.type ) {
      throw new BadImplementationException(
        'satwsService.processEvent.type.null' )
    }

   if ( !dto.data.object.id ) {
      throw new BadImplementationException(
        'satwsService.processEvent.credentialId.null' )
    }

   if ( !dto.data.object.status ) {
      throw new BadImplementationException(
        'satwsService.processEvent.type.null' )
    }

  }

  private void processFailure( SatwsEventDto dto ) throws Exception {

    def financialInstitution = financialInstitutionService.findOneByCode( SATWS_CODE )
    if ( !financialInstitution ) {
      throw new BadImplementationException(
        'satwsService.processFailure.financialInstitution.notFound' )
    } 

    def credential = credentialService
      .findByScrapperCredentialIdAndInstitution(
        dto.data.object.id, financialInstitution)

   	def failureDto = new FailureCallbackDto( 
   		data: new FailureCallbackData(
   			 credential_id: credential.id,
   			 error_message: WRONG_CREDENTIAL_MESSAGE,
   			 status_code: WRONG_CREDENTIAL_CODE) )
    
    credentialFailureService.processFailure( failureDto )    
    println 'after procesing failure'

  }


  Map getInvoicesByParams( Map params ) throws Exception {

     if ( !params.customerId ) {
      throw new BadImplementationException(
        'satwsService.getInvoicesByParams.params.customerId.null' )
    }     

    def financialInstitution = financialInstitutionService.findOneByCode( SATWS_CODE )
    if ( !financialInstitution ) {
      throw new BadImplementationException(
        'satwsService.getInvoicesByParams.financialInstitution.notFound' )
    }      
    
    def customer = customerService.findOne( params.customerId as Long )
    if ( !customer ) {
      throw new BadImplementationException(
        'satwsService.getInvoicesByParams.customer.notFound' )
    }

    def credential = credentialService.findByCustomerAndFinancialIntitution( customer, financialInstitution )
    if ( !credential ) {
      throw new BadImplementationException(
        'satwsService.getInvoicesByParams.credential.notFound' )
    }      
     def rfc = credential.username    
     params.customerId = null
     satwsClientService.getInvoicesByParams( rfc, params )

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

}
