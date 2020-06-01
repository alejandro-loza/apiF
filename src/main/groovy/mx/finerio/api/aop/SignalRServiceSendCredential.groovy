package mx.finerio.api.aop

import mx.finerio.api.domain.*

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
class SignalRServiceSendCredential {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SignalRServiceSendCredential' )

  @Pointcut(
    value='execution(java.lang.String mx.finerio.api.services.SignalRService.sendCredential(..)) && bean(signalRService) && args(credentialData)',
    argNames='credentialData'
  )
  public void sendCredential( Map credentialData ) {}

  @Before('sendCredential(credentialData)')
  void before( Map credentialData ) {
    log.info( "<< credentialData: {}", credentialData )
  }

  @AfterReturning(
    pointcut='sendCredential(java.util.Map)',
    returning='connectionId'
  )
  void afterReturning( String connectionId ) {
    log.info( '>> connectionId: {}', connectionId )
  }

  @AfterThrowing(
    pointcut='sendCredential(java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
