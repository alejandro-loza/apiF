package mx.finerio.api.aop
  
import mx.finerio.api.domain.Credential
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component

@Component
@Aspect
class CredentialServiceUpdateProviderId {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceUpdateProviderId' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Credential mx.finerio.api.services.CredentialService.updateProviderId(..)) && bean(credentialService) && args(id, providerId)',
    argNames='id, providerId'
  )
  public void updateProviderId( String id, String providerId ) {}

  @Before('updateProviderId(id, providerId)')
  void before( String id, String providerId ) {
    log.info( "<< id: {}, providerId: {}", id, providerId )
  }

  @AfterReturning(
    pointcut='updateProviderId(String,String)',
    returning='response'
  )
  void afterReturning( Credential response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='updateProviderId(String,String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
