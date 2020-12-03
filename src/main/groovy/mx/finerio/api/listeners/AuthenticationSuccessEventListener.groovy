package mx.finerio.api.listeners

import org.springframework.stereotype.Component
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.beans.factory.annotation.Autowired
import mx.finerio.api.services.LoginAttemptService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Component
public class AuthenticationSuccessEventListener 
  implements ApplicationListener<AuthenticationSuccessEvent> {

  	final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.listeners.AuthenticationSuccessEventListener' )
 
    @Autowired
    private LoginAttemptService loginAttemptService;
 
    public void onApplicationEvent(AuthenticationSuccessEvent e) {
     
      try {

	    def username = e.getAuthentication().getDetails().username
        loginAttemptService.loginSucceeded( username )       

      }catch( groovy.lang.MissingPropertyException ex ){
        log.info( "Error on trying to clear attempts: {}", ex.message )
      }catch( Exception ex ){
        log.info( "Error: {}", ex.message )
      }
             
    }
}