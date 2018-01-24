package mx.finerio.api.aop

import mx.finerio.api.dtos.*

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
class CredentialServiceCreateCredential {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceCreateCredential' )

  @Pointcut(
    value='execution(Object mx.finerio.api.services.CredentialService.createCredential(..)) && bean(credentialService) && args(credential)',
    argNames='credential'
  )
  public void createCredential( CredentialDto credential ) {}

  @Before('createCredential(credential)')
  void before( CredentialDto credential ) {
    log.info( "<< credential: {}", credential )
  }

  @AfterReturning(
    pointcut='createCredential(mx.finerio.api.dtos.CredentialDto)',
    returning='response'
  )
  void afterReturning( Object response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='createCredential(mx.finerio.api.dtos.CredentialDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
