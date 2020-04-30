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
class SignalRServiceSendTokenToScrapper {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SignalRServiceSendTokenToScrapper' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.SignalRService.sendTokenToScrapper(..)) && bean(signalRService) && args(token,credentialId)',
    argNames='token,credentialId'
  )
  public void sendTokenToScrapper( String token, String credentialId  ) {}

  @Before('sendTokenToScrapper(token,credentialId)')
  void before( String token, String credentialId  ) {
    log.info( "<< token: {} credentialId:{}", token, credentialId )
  }

  @AfterReturning(
    pointcut='sendTokenToScrapper(String,String)'    
  )
  void afterReturning() {
    log.info( '>> ok' )
  }

  @AfterThrowing(
    pointcut='sendTokenToScrapper(String,String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
