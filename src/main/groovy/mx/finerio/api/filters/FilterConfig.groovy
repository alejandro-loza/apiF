package mx.finerio.api.filters

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig {

  @Bean
  FilterRegistrationBean credentialFilterRegistrationBean() {

    def registrationBean = new FilterRegistrationBean()
    registrationBean.name = 'credential'
    registrationBean.setUrlPatterns( [
      '/credentials/*', '/credentials' ] )
    registrationBean.filter = getCredentialFilter()
    registrationBean

  }

  @Bean
  CredentialFilter getCredentialFilter() {
    new CredentialFilter()
  }

  @Bean
  CorsFilter getCors() {
	new CorsFilter()
  }
  
}
