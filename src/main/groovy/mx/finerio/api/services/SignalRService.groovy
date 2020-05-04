package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.Action1
import groovy.json.JsonSlurper
import static java.nio.charset.StandardCharsets.*
import org.springframework.scheduling.annotation.Async
import io.reactivex.observers.DisposableCompletableObserver
import com.google.gson.internal.LinkedTreeMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PreDestroy

@Service
class SignalRService {

	final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.services.SignalRService' )
	
	String url		
	String pathRequired	
	String tokenRequiredListener
	String pathReceived

	HubConnection connection
	
	@Autowired
	RestTemplateService restTemplateService
	
	@Autowired
	CallbackService callbackService
  
	@Autowired
	CredentialService credentialService


	@Autowired
 	public TransactionsReceiver( 
 		@Value('${scrapper.signalR.rest.connection.url}') final String url, 
    	@Value('${scraper.rest.tokenRequired.path}') final String pathRequired,    	
    	@Value('${scraper.rest.tokenReceived.path}') final String pathReceived  ){
    
 		this.url = url
    	this.pathRequired = pathRequired    	
    	this.pathReceived = pathReceived
    
     	this.createConnection()
 	}

	private void createConnection( Map credentialData ) {		

		connection = HubConnectionBuilder.create( this.url ).build()

		connection.on( 'token_required', 
			{ data  -> 		                   
                this.validateMessage( data )
			    this.onTokenRequired( data )	         			
			}, LinkedTreeMap.class)

		connection.on( 'token_received', //Consuming this for not showing errors on logs
			{ data  -> }, LinkedTreeMap.class)

		connection.start().subscribe(
			{ 
				log.info("-- Connection with signalRService Started --") 
			},
			{ 
				e -> log.info("-- Error when connecting with signalRService ${e.message}--")
		    })
	}

	private validateMessage( LinkedTreeMap data ){
  		if( !data.Id ){
    		throw new BadImplementationException(
        	'signalRService.validateMessage.data.null' )  			
  		}

	} 	

	 private void onTokenRequired( LinkedTreeMap data ) {
		  	
		  String credentialId = data.Id		  						 
		  Credential credential = credentialService.findAndValidate( credentialId )		  
		  callbackService.sendToClient( credential.customer.client,
			  Callback.Nature.NOTIFY, [ credentialId: credentialId,
		      stage: 'Token' ] )		 
	
	  }	
    @Async
	String sendCredential( Map credentialData ) {
				
  		validateCredentialData( credentialData )

      	def finalUrl = "${url}/${pathRequired}"		 		
		restTemplateService.post( finalUrl, [:], credentialData )

        'Credentail sent successfully'
	}


	private void validateCredentialData( Map credentialData ){
    	if( !credentialData){
    		throw new BadImplementationException(
        	'signalRService.validateCredentialData.credentialData.null' )
    	}
	} 
		
	  		  
	@Async
	void sendTokenToScrapper( String token, String credentialId )  {
		
		def finalUrl = "${url}/${pathReceived}"
		def data = [
					 Id: credentialId,
			         Token: token,
			         State: 'Token'
					]		  
		restTemplateService.post( finalUrl, [:], data )				
	 }

	  @PreDestroy	 	  	
	  private void closeConnection( ) {		  
		  connection.stop().subscribe(
			{ 
				log.info("-- Connection with signalRService Closed --") 
			},
			{ 
				e -> log.info("-- Error when closing signalRService ${e.message}--")
		    })
	  }
}
	  

