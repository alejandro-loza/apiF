package mx.finerio.api.aop

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
class CredentialServiceSetFailure {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceSetFailure' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.CredentialService.setFailure(..)) && bean(credentialService) && args(credentialId, message)',
    argNames='credentialId, message'
  )
  public void setFailure( String credentialId, String message ) {}

  @Before('setFailure(credentialId, message)')
  void before( String credentialId, String message ) {
    log.info( "<< credentialId: {}, message: {}", credentialId, message )
  }

  @AfterReturning(
    pointcut='setFailure(String, String)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='setFailure(String, String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
