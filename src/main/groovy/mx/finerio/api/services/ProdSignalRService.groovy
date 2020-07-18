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
import org.springframework.context.annotation.Profile

@Service
@Profile('prod')
class ProdSignalRService implements SignalRService {

	final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.services.ProdSignalRService' )
	
	String url		
	String pathReceived

	HubConnection connection
	
	@Autowired
	RestTemplateService restTemplateService
	
	@Autowired
	CallbackService callbackService
  
	@Autowired
	CredentialService credentialService

	@Autowired
	CredentialTokenService credentialTokenService

	@Autowired
 	public TransactionsReceiver( 
 		@Value('${scrapper.signalR.rest.connection.url}') final String url,     	
    	@Value('${scraper.rest.tokenReceived.path}') final String pathReceived  ){
    
 		this.url = url   	
    	this.pathReceived = pathReceived
    
     	this.createConnection()
 	}

	private void createConnection( Map credentialData ) {		

		connection = HubConnectionBuilder.create( this.url ).build()

		connection.on( 'token_required', 
			{ 
			  data  -> 	
			    try{				                  
	                    	this.validateMessage( data )
				this.onTokenRequired( data )	         			
			    }catch(BadImplementationException e){
	                        log.info("Credential id is missing: ${e.message}") 
			    }
			     catch(Exception e){
	                        log.info("Error: ${e.message}") 
			    }
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
  		if( !data.id ){
    	          throw new BadImplementationException(
        	  'signalRService.validateMessage.data.id.null' )  			
  		}

  		if( !data.clientId ){
    	          throw new BadImplementationException(
        	  'signalRService.validateMessage.data.clientId.null' )  			
  		}

	} 	

	 private void onTokenRequired( LinkedTreeMap data ) {
		  	
		  String credentialId = data.id
		  log.info("onTokenRequiredEvent with credentialId: ${credentialId}") 		  						 
		  Credential credential = credentialService.findAndValidate( credentialId )
		  def dataSend = [ credentialId: credentialId, stage: 'interactive' ]		  
		  if( data.bankToken ) { 
                    dataSend.put('bankToken', data.bankToken as Integer )
		  }
		  credentialTokenService.saveUpdateCredentialToken( credentialId, data.clientId )
		  callbackService.sendToClient( credential.customer.client,
			  Callback.Nature.NOTIFY, dataSend )		 
	
	  }	


	private void validateCredentialData( Map credentialData ){
    	 if( !credentialData){
    		throw new BadImplementationException(
        	'signalRService.validateCredentialData.credentialData.null' )
    	 }
	} 
		
	  		  
	@Async
	@Override
	void sendTokenToScrapper( String token, String credentialId )  {
		
		def finalUrl = "${url}/${pathReceived}"
		def tokenClientId = credentialTokenService.findTokenClientIdByCredentilId( credentialId )
		def data = [
					 id: credentialId,
			         token: token,
			         clientId: tokenClientId 			         
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
	  

