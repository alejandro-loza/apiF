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
class CredentialServiceUpdateStatus {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceUpdateStatus' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Credential mx.finerio.api.services.CredentialService.updateStatus(..)) && bean(credentialService) && args(credentialId, status)',
    argNames='credentialId, status'
  )
  public void updateStatus( String credentialId, Credential.Status status ) {}

  @Before('updateStatus(credentialId, status)')
  void before( String credentialId, Credential.Status status ) {
    log.info( "<< credentialId: {}, status: {}", credentialId, status )
  }

  @AfterReturning(
    pointcut='updateStatus(String, mx.finerio.api.domain.Credential.Status)',
    returning='credential'
  )
  void afterReturning( Credential credential ) {
    log.info( '>> credential: {}', credential )
  }

  @AfterThrowing(
    pointcut='updateStatus(String, mx.finerio.api.domain.Credential.Status)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
