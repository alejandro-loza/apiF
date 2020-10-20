package mx.finerio.api.oauth2

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer

@Configuration
@EnableResourceServer
class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  @Override
  void configure( HttpSecurity http ) throws Exception {

    http
      .authorizeRequests()
      .antMatchers( '/callbacks/**' ).permitAll()
      .antMatchers( '/password/**' ).permitAll()
      .antMatchers( '/Jc79e49K964wK6pBWsHW6hw9SUW5jYytb8NR9Q8ZwrVpSrXFdK' ).permitAll()
      .antMatchers( '/j2GVbQs3kkcBEttuPWZihSFZkoWnIDwQt2zsGRmQZoitHzMllB' ).permitAll()
      .anyRequest().authenticated()
	  
  }

  @Override
  void configure( ResourceServerSecurityConfigurer resources)  throws Exception {
    resources.resourceId( 'resource_id' )
  }

}