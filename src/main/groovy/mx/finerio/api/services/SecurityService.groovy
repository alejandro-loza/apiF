package mx.finerio.api.services

import mx.finerio.api.domain.repository.ClientRepository
import mx.finerio.api.exceptions.BadRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class SecurityService {

  @Autowired
  UserDetailsService userDetailsService

  @Autowired
  ClientRepository clientRepository

  UserDetails getCurrent() throws Exception {

    userDetailsService.loadUserByUsername(
        SecurityContextHolder.context.authentication.name )

  }

  void validateInsightsEnabled() throws Exception {
    if ( !clientRepository.findOneByUsername(SecurityContextHolder.context
            .authentication.name).insightsEnabled ) {
      throw new BadRequestException( 'clients.insights.disabled' )
    }
  }

}
