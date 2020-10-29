package mx.finerio.api.aop

import mx.finerio.api.domain.CredentialState

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
class CredentialStateServiceAddState {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialStateServiceAddState' )

  @Pointcut(
    value='execution(Boolean mx.finerio.api.services.CredentialStateService.addState(..)) && bean(credentialStateService) && args(credentialId, data)',
    argNames='credentialId, data'
  )
  public void save( String credentialId, Map data ) {}

  @Before('save(credentialId, data)')
  void before( String credentialId, Map data ) {
    log.info( "<< credentialId: {}, data: {}", credentialId, data )
  }

  @AfterReturning(
    pointcut='save(String, java.util.Map)',
    returning='added'
  )
  void afterReturning( Boolean added ) {
    log.info( '>> added: {}', added )
  }

  @AfterThrowing(
    pointcut='save(String, java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
