package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.repository.ClientRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import mx.finerio.api.exceptions.UserBlockedException

@Service
class MyUserDetailsService implements UserDetailsService {

  @Autowired
  ClientRepository clientRepository

  @Autowired
  LoginAttemptService loginAttemptService

  @Override
  UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {

    if( loginAttemptService.isBlocked( username ) ){
      throw new RuntimeException('user.is.blocked')
    }

    def instance = clientRepository.findOneByUsername( username )

    if ( !instance ) {
      throw new UsernameNotFoundException( 'user.not.found' )
    }

    instance

  }

}
