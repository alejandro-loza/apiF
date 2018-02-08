package mx.finerio.api.aop

import mx.finerio.api.domain.Credential
import mx.finerio.api.dtos.CredentialDto

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
class CredentialServiceCreate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceCreate' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Credential mx.finerio.api.services.CredentialService.create(..)) && bean(credentialService) && args(credentialDto)',
    argNames='credentialDto'
  )
  public void create( CredentialDto credentialDto ) {}

  @Before('create(credentialDto)')
  void before( CredentialDto credentialDto ) {
    log.info( "<< credentialDto: {}", credentialDto )
  }

  @AfterReturning(
    pointcut='create(mx.finerio.api.dtos.CredentialDto)',
    returning='response'
  )
  void afterReturning( Credential response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(mx.finerio.api.dtos.CredentialDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
