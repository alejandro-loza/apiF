package mx.finerio.api.aop

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import mx.finerio.api.domain.Client

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component

@Component
@Aspect
class CredentialServiceRequestData2 {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceRequestData2' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.CredentialService.requestData(..)) && bean(credentialService) && args(credentialId,client)',
    argNames='credentialId,client'
  )
  public void requestData( String credentialId, Client client ) {}

  @Before('requestData(credentialId,client)')
  void before( String credentialId, Client client  ) {
    log.info( "<< credentialId: {} client:{}", credentialId, client )
  }

  @AfterReturning(
    pointcut='requestData(String,mx.finerio.api.domain.Client)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='requestData(String,mx.finerio.api.domain.Client)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
