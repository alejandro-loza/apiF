package mx.finerio.api.aop

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Client

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
class CredentialServiceFindOne2 {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceFindOne2' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Credential mx.finerio.api.services.CredentialService.findOne(..)) && bean(credentialService) && args(id,client)',
    argNames='id,client'
  )
  public void findOne( String id, Client client ) {}

  @Before('findOne(id,client)')
  void before( String id, Client client ) {
    log.info( "<< id: {}, client: {}", id, client )
  }

  @AfterReturning(
    pointcut='findOne(String, mx.finerio.api.domain.Client)',
    returning='response'
  )
  void afterReturning( Credential response  ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOne(String,mx.finerio.api.domain.Client)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
