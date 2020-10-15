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
class CredentialStateServiceSave {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialStateServiceSave' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.CredentialState mx.finerio.api.services.CredentialStateService.save(..)) && bean(credentialStateService) && args(credentialId, state)',
    argNames='credentialId, state'
  )
  public void save( String credentialId, String state ) {}

  @Before('save(credentialId, state)')
  void before( String credentialId, String state ) {
    log.info( "<< credentialId: {}, state: {}", credentialId, state )
  }

  @AfterReturning(
    pointcut='save(String, String)',
    returning='credentialState'
  )
  void afterReturning( CredentialState credentialState ) {
    log.info( '>> credentialState: {}', credentialState )
  }

  @AfterThrowing(
    pointcut='save(String, String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
