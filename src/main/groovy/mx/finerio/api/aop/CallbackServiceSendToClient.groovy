package mx.finerio.api.aop

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Callback

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
class CallbackServiceSendToClient {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CallbackServiceSendToClient' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.CallbackService.sendToClient(..)) && bean(callbackService) && args(client, nature, data)',
    argNames='client, nature, data'
  )
  public void sendToClient( Client client, Callback.Nature nature, Map data ) {}

  @Before('sendToClient(client, nature, data)')
  void before( Client client, Callback.Nature nature, Map data ) {
    log.info( "<< client: {}, nature: {}, data: {}", client, nature, data )
  }

  @AfterReturning(
    pointcut='sendToClient(mx.finerio.api.domain.Client, mx.finerio.api.domain.Callback.Nature, java.util.Map)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='sendToClient(mx.finerio.api.domain.Client, mx.finerio.api.domain.Callback.Nature, java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
