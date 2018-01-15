package mx.finerio.api.services

import mx.finerio.api.domain.repository.UserRepository
import mx.finerio.api.domain.User

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class MyUserDetailsService implements UserDetailsService {

  @Autowired
  UserRepository userRepository

  @Override
  UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
    userRepository.findOneByUsername( username )
  }

  User findById(String id){
    userRepository.findById(id)
  }

}
