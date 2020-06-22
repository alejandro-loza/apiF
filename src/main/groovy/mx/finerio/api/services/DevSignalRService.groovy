package mx.finerio.api.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PreDestroy
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

@Service
@Profile('dev')
class DevSignalRService implements SignalRService{

	final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.services.DevSignalRService' )
		
	@Autowired
 	public TransactionsReceiver(){
 		log.info("Dummy Connection to signalr set.") 	
 	}


	
	@Override
	void sendTokenToScrapper( String token, String credentialId )  {
	  	log.info("Not sendign token to scrapper cause de connection is dummy")	
			
	 }
				 
}
	  

