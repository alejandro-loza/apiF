package mx.finerio.api.services


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import groovy.json.JsonSlurper
import static java.nio.charset.StandardCharsets.*
@Service
class SignalService {

	Map connections = [:]
	
	@Value( '${signalR.connection.url}' )
	String url

	void createConnection( String credentialId ) {
		HubConnection connection = HubConnectionBuilder.create( url ).build()
		
		connection.on("token_required", (message) -> {
			System.out.println("New Message: " + message)
		}, String.class)
		
		connections.put( connection.getConnectionId(), connection )
	}
	/*
	 "connectionId" : "TyjuNGBBQlHwPvBunaHdGAc29a6b301",
    "Id" :"e7a222d9-e9f7-4e18-936b-3bbbc7cc1bdf",
    "Username": "JLOPEZA198271",
    "Password": "11f7df49ebd4f6ee16a4250fe22b42cce4e63e7c6ede54ac814cff",
    "IV": "3a4b8faffa9eb72fc48685dc",
    "State": "Start",
    "User" : {
        "Id": "b32587ed-a5bb-4d04-8238-1b0d6687f901"
        },
    "Institution": { "Id" : "14" }
}
	 * 
	 * */
	
	  
	  void onTokenRequired( String message ) {
		  
		  def messageMap = new JsonSlurper().
		  				parseText( new String( message, UTF_8) )
		  
	  }
	  
	  
	  void sendAccountDataToScrapper() {
		  
	  }
	  
	  void sendTokenToScrapper() {
		  
	  }
	  
	  void closeConnection() {
		  
	  }

}
