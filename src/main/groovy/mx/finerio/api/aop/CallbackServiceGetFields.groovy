package mx.finerio.api.aop

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
class CallbackServiceGetFields {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CallbackServiceGetFields' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.CallbackService.getFields(..)) && bean(callbackService) && args(callback)',
    argNames='callback'
  )
  public void getFields( Callback callback ) {}

  @Before('getFields(callback)')
  void before( Callback callback ) {
    log.info( "<< callback: {}", callback )
  }

  @AfterReturning(
    pointcut='getFields(mx.finerio.api.domain.Callback)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getFields(mx.finerio.api.domain.Callback)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
