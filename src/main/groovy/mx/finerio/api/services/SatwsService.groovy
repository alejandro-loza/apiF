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
  final static String WRONG_CREDENTIAL_CODE = "401"
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
        def type = dto.type

        switch( type ) {
        	case 'invalid':
        		processFailure( dto )
        	break
        }   

  }

  private void processFailure( SatwsEventDto dto ) throws Exception {

   	def failureDto = new FailureCallbackDto( 
   		data: new FailureCallbackData(
   			 credential_id: dto.id,
   			 error_message: WRONG_CREDENTIAL_CODE, 
   			 status_code: WRONG_CREDENTIAL_MESSAGE) )
    
    credentialFailureService.processFailure( failureDto )    

  }


  Map getInvoicesByParams( Map params ) throws Exception {
     //TODO validate input

     def financialInstitution = financialInstitutionService.findOneByCode( SATWS_CODE )
     def customer = customerService.findOne( params.customerId as Long )
     def credential = credentialService.findByCustomerAndFinancialIntitution( customer, financialInstitution )
     def rfc = credential.username    

     params.customerId = null
     satwsClientService.getInvoicesByParams( rfc, params )
  }


  String getInvoice( String invoiceId, String accept ) throws Exception {
    //TODO validate input 
   satwsClientService.getInvoice( invoiceId, accept )
  }
}
