package mx.finerio.api.aop

import mx.finerio.api.domain.Credential
import mx.finerio.api.dtos.CredentialUpdateDto

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
class CredentialServiceUpdate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceUpdate' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Credential mx.finerio.api.services.CredentialService.update(..)) && bean(credentialService) && args(id, credentialUpdateDto)',
    argNames='id, credentialUpdateDto'
  )
  public void update( String id, CredentialUpdateDto credentialUpdateDto ) {}

  @Before('update(id, credentialUpdateDto)')
  void before( String id, CredentialUpdateDto credentialUpdateDto ) {
    log.info( "<< id: {}, credentialUpdateDto: {}", id, credentialUpdateDto )
  }

  @AfterReturning(
    pointcut='update(String, mx.finerio.api.dtos.CredentialUpdateDto)',
    returning='response'
  )
  void afterReturning( Credential response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='update(String, mx.finerio.api.dtos.CredentialUpdateDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
