package mx.finerio.api.aop

import mx.finerio.api.dtos.CallbackUpdateDto
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
class CallbackServiceUpdate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CallbackServiceUpdate' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Callback mx.finerio.api.services.CallbackService.update(..)) && bean(callbackService) && args(id, callbackUpdateDto)',
    argNames='id, callbackUpdateDto'
  )
  public void update( Long id, CallbackUpdateDto callbackUpdateDto ) {}

  @Before('update(id, callbackUpdateDto)')
  void before( Long id, CallbackUpdateDto callbackUpdateDto ) {
    log.info( "<< id: {}, callbackUpdateDto: {}", id, callbackUpdateDto )
  }

  @AfterReturning(
    pointcut='update(Long, mx.finerio.api.dtos.CallbackUpdateDto)',
    returning='response'
  )
  void afterReturning( Callback response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='update(Long, mx.finerio.api.dtos.CallbackUpdateDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
