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
class CredentialServiceDelete {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceDelete' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.CredentialService.delete(..)) && bean(credentialService) && args(id)',
    argNames='id'
  )
  public void deleteMethod( String id ) {}

  @Before('deleteMethod(id)')
  void before( String id ) {
    log.info( "<< credentialDto: {}", id )
  }

  @AfterReturning(
    pointcut='deleteMethod(String)'
  )
  void afterReturning() {
    log.info( '>> response: OK' )
  }

  @AfterThrowing(
    pointcut='deleteMethod(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
