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
class CredentialServiceRequestData {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceRequestData' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.CredentialService.requestData(..)) && bean(credentialService) && args(credentialId)',
    argNames='credentialId'
  )
  public void requestData( String credentialId ) {}

  @Before('requestData(credentialId)')
  void before( String credentialId ) {
    log.info( "<< credentialId: {}", credentialId )
  }

  @AfterReturning(
    pointcut='requestData(String)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='requestData(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
