package mx.finerio.api.aop

import mx.finerio.api.domain.*

import org.aspectj.lang.annotation.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component

@Component
@Aspect
class CredentialStatusHistoryServiceUpdate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialStatusHistoryServiceUpdate' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.CredentialStatusHistoryService.update(..)) && bean(credentialStatusHistoryService) && args(credential)',
    argNames='credential'
  )
  public void update( Credential credential ) {}

  @Before('update(credential)')
  void before( Credential credential ) {
    log.info( "<< credential: {}", credential )
  }

  @AfterReturning(
    pointcut='update(mx.finerio.api.domain.Credential)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='update(mx.finerio.api.domain.Credential)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
