package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.repository.ClientRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class MyUserDetailsService implements UserDetailsService {

  @Autowired
  ClientRepository clientRepository

  @Override
  UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {

    def instance = clientRepository.findOneByUsername( username )

    if ( !instance ) {
      throw new UsernameNotFoundException( 'user.not.found' )
    }

    instance

  }

}
