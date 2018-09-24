package mx.finerio.api.aop

import mx.finerio.api.domain.*

import org.aspectj.lang.annotation.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component

@Component
@Aspect
class CredentialStatusHistoryServiceCreate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialStatusHistoryServiceCreate' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.CredentialStatusHistory mx.finerio.api.services.CredentialStatusHistoryService.create(..)) && bean(credentialStatusHistoryService) && args(credential)',
    argNames='credential'
  )
  public void create( Credential credential ) {}

  @Before('create(credential)')
  void before( Credential credential ) {
    log.info( "<< credential: {}", credential )
  }

  @AfterReturning(
    pointcut='create(mx.finerio.api.domain.Credential)',
    returning='response'
  )
  void afterReturning( CredentialStatusHistory response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(mx.finerio.api.domain.Credential)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
