package mx.finerio.api.listeners

import org.springframework.stereotype.Component
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.beans.factory.annotation.Autowired
import mx.finerio.api.services.LoginAttemptService

@Component
public class AuthenticationSuccessEventListener 
  implements ApplicationListener<AuthenticationSuccessEvent> {
 
    @Autowired
    private LoginAttemptService loginAttemptService;
 
    public void onApplicationEvent(AuthenticationSuccessEvent e) {
     
        def username = e.getAuthentication().getDetails().username
        loginAttemptService.loginSucceeded( username )

    }
}