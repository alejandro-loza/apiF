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

@Service
class SignalRService {

	  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.services.SignalRService' )

	Map connections = [:]
	
	@Value( '${scrapper.signalR.connection.url}' )
	String url
	
	@Value( '${scraper.rest.connection.url}' )
	String urlRestScrapper
	
	@Value( '${scraper.rest.tokenRequired.path}' )
	String pathRequired
	
	@Value( '${scraper.rest.tokenRequired.listener}' )
	String tokenRequiredListener
    
	@Value( '${scraper.rest.tokenReceived.path}' )
	String pathReceived
	
	@Autowired
	RestTemplateService restTemplateService
	
	@Autowired
	CallbackService callbackService
  
	@Autowired
	CredentialService credentialService

	String sendCredential( Map credentialData ) {
				
      	validateCredentialData( credentialData )
		String connectionId = this.createConnection( credentialData )		
		credentialData.connectionId = connectionId				
		this.sendCredentialDataToScrapper( credentialData )
		connectionId			
	}

	private void validateCredentialData( Map credentialData ){
    	if( !credentialData){
    		throw new BadImplementationException(
        	'signalRService.validateCredentialData.credentialData.null' )
    	}
	} 
		
	private String createConnection( Map credentialData ) {		
		HubConnection connection = HubConnectionBuilder.create( this.url ).build()

		connection.on( 'token_required', { data  -> 		
            log.info(" -- Message from signalR service ${data} --")
			onTokenRequired( data )
		}, LinkedTreeMap.class)
	
		connection.start().blockingGet()

		def connectionId = connection.getConnectionId()
			connections.put( connectionId,  [ connection: connection,
			credentialId: credentialData.Id ] )

		connectionId	
	}
	  	
	 @Async
	 private void sendCredentialDataToScrapper( Map credentialData ) {
		  
		 def finalUrl = "${urlRestScrapper}/${pathRequired}"
		 def headers = [:]
		 def body = credentialData
		 restTemplateService.post( finalUrl, headers, body )
		  		  
	  }
	   
	 private void onTokenRequired( LinkedTreeMap data ) {
		  
		if ( data.State.equals('Token') ){

		  String connectionId = data.connectionId
		  String credentialId = connections.get( connectionId ).get( 'credentialId' ) 				  
						  
		  Credential credential = credentialService.findAndValidate( credentialId )
		  def client = credential.customer.client
		  callbackService.sendToClient( client,
			  Callback.Nature.NOTIFY, [ credentialId: credentialId,
		      stage: data?.State ] )

		  closeConnection( connectionId )
		}		
	  }
	  	 	  	
	  private void closeConnection( String connectionId ) {
		  HubConnection connection = this.connections.
		  		get( connectionId ).get('connection') 
		  connection.stop()				 
	  }
	  
	  private void removeConnection( String connectionId ) {
		  this.connections.remove( connectionId )
	  }
	  
	  @Async
	  void sendTokenToScrapper( String token, String credentialId )  {
		  String connectionId = getConnectionIdFromCredentialId( credentialId )
		  def finalUrl = "${urlRestScrapper}/${pathReceived}"
		  def data = [ connectionId: connectionId,
			              Token: token]		  
		  restTemplateService.post( finalUrl, [:], data )
		  removeConnection( connectionId )
		  
	  }
	  
	  private String getConnectionIdFromCredentialId( String credentialId ) {		 
		  
		  for ( String connectionId : this.connections.keySet() ){
			  String internCredentialId = 
			  	this.connections.get(connectionId).get('credentialId')
				  if( internCredentialId.equals(credentialId)) {
					  return connectionId
				  }
			  }
		  	
      	throw new BadImplementationException(
        	'signalRService.getConnectionIdFromCredentialId.connectionId.notFound' )
    			  		 
	  }

}
