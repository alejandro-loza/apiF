package mx.finerio.api.listeners

import org.springframework.stereotype.Component
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.beans.factory.annotation.Autowired
import mx.finerio.api.services.LoginAttemptService

@Component
public class AuthenticationFailureListener 
  implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

  	@Autowired
  	LoginAttemptService loginAttemptService

    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {

    	def username = e.getAuthentication().getDetails().username
        loginAttemptService.loginFailed( username )

    }

}