package mx.finerio.api.oauth2

import javax.sql.DataSource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer

@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

  @Autowired
  UserDetailsService userDetailsService

  @Autowired
  AuthenticationManager authenticationManager

  @Autowired
  DataSource dataSource

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder()
  }

  @Override
  void configure( AuthorizationServerEndpointsConfigurer configurer )
      throws Exception {

    configurer.authenticationManager( authenticationManager )
    configurer.userDetailsService( userDetailsService )

  }

  @Override
  void configure( ClientDetailsServiceConfigurer clients ) throws Exception {
    clients.jdbc( dataSource )
  }

}
