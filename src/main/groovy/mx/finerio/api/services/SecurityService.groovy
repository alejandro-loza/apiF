package mx.finerio.api.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class SecurityService {

  @Autowired
  UserDetailsService userDetailsService

  UserDetails getCurrent() throws Exception {

    userDetailsService.loadUserByUsername(
        SecurityContextHolder.context.authentication.name )

  }

}
