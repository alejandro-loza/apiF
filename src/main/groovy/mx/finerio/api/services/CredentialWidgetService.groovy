package mx.finerio.api.services


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientWidget
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.ClientWidgetRepository
import mx.finerio.api.dtos.CustomerDto
import mx.finerio.api.dtos.CredentialDto
import mx.finerio.api.dtos.CredentialWidgetDto

@Service
class CredentialWidgetService {

  @Autowired
  ClientWidgetRepository clientWidgetRepository

  @Autowired
  CustomerService customerService

  @Autowired
  CredentialService credentialService


  Map create( CredentialWidgetDto credentialWidgetDto ) throws Exception {

  	validate( credentialWidgetDto )
    ClientWidget clientWidget = clientWidgetRepository
    		.findByWidgetId( credentialWidgetDto.widgetId )

    if( !clientWidget ){
        throw new BadRequestException( 'widget.id.not.found' )
    }
   
 	Customer customer
    if( credentialWidgetDto.customerId ) {    	
    	customer = customerService
    				.findOne( credentialWidgetDto.customerId, clientWidget.client ) 
    }else if( credentialWidgetDto.customerName ) {
        customer = findCustomer( clientWidget.client, credentialWidgetDto.customerName )
    }

    def credentialDto = this.getCredentialDto( customer.id, credentialWidgetDto )
    def instance = credentialService.create( credentialDto, customer, clientWidget.client )
    instance = credentialService.getFields( instance )
    instance
  }

  private void validate( CredentialWidgetDto credentialWidgetDto ) throws Exception {
    
    if ( !credentialWidgetDto ) {
      throw new BadRequestException(
          'credentialService.create.credentialDto.null' )
    }

    if( !credentialWidgetDto.customerId &&
    	!credentialWidgetDto.customerName )	{
    	throw new BadRequestException(
          'credentialWidgetService.create.validate.customerId.customerName.null' )
  	}
  }

  private getCredentialDto(Long customerId, CredentialWidgetDto credentialWidgetDto ) throws Exception {
    new CredentialDto(
    	username: credentialWidgetDto.username,
    	password: credentialWidgetDto.password,
    	securityCode: credentialWidgetDto.securityCode,
    	bankId: credentialWidgetDto.bankId,
    	customerId: customerId,
    	state: credentialWidgetDto.state)
  }

  private Customer findCustomer( Client client, String name )
      throws Exception {

    def customer = customerService.findByName( client, name )

    if ( customer ==  null ) {

      def customerDto = new CustomerDto( name: name )
      customer = customerService.create( customerDto, client )

    }

    return customer

  }

}
