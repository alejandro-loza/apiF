package mx.finerio.api.oauth2

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer

import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableResourceServer
class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  @Override
  void configure( HttpSecurity http ) throws Exception {

    http
      .cors().and()
      .authorizeRequests()
      .antMatchers( '/callbacks/**' ).permitAll()
      .antMatchers( '/password/**' ).permitAll()
      .antMatchers( '/Jc79e49K964wK6pBWsHW6hw9SUW5jYytb8NR9Q8ZwrVpSrXFdK' ).permitAll()
      .antMatchers( '/j2GVbQs3kkcBEttuPWZihSFZkoWnIDwQt2zsGRmQZoitHzMllB' ).permitAll()
      .antMatchers( '/p8U55qGnTMLb7HQzZfCjwcQARtVrrgyt8he9fQKz3KgAFPbAwb' ).permitAll()
      .antMatchers( '/kFYfkW3wK65ZeXHQ46kjeF9wrZTKuR5NjUR8G6k37LMs2a9YHM' ).permitAll()
      .antMatchers( '/kTSZhDQdAgU9SHlPldja5enR7IHMZgT7YzU30A4qHPonmz2vCb' ).permitAll()
      .antMatchers( '/8jyY41afTYFyOlyEjzZvqsqxnMEvIinHrJtkztpifVGR62kds9' ).permitAll()
      .antMatchers( '/0M4iZuRoR5RYBZ3bVb8KFeP2epHHtVJjvDpkV5xC0epr3irOhp' ).permitAll()
      .antMatchers( '/lRyxTx5STdiH9fvSg00GYllSreQoEinP0KcH75pO8wk7BFPFuD' ).permitAll()
      .antMatchers( '/FM73Eptuzq24683NNg37GeCHHme4KdaUa2d46S89FCp9MCTnfw' ).permitAll()    
      .anyRequest().authenticated()            
  }

  @Override
  void configure( ResourceServerSecurityConfigurer resources)  throws Exception {
    resources.resourceId( 'resource_id' )
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {

    def source = new UrlBasedCorsConfigurationSource()
    def config = new CorsConfiguration()
    config.setAllowCredentials( true )
    config.addAllowedOrigin( '*' )
    config.addAllowedHeader( '*' )
    config.addAllowedMethod( 'OPTONS' )
    config.addAllowedMethod( 'GET' )
    config.addAllowedMethod( 'POST' )
    config.addAllowedMethod( 'PUT' )
    config.addAllowedMethod( 'DELETE' )
    source.registerCorsConfiguration( '/**', config )
    return source

  }

}

