package mx.finerio.api.aop

import mx.finerio.api.dtos.CallbackDto
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
class CallbackServiceCreate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CallbackServiceCreate' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Callback mx.finerio.api.services.CallbackService.create(..)) && bean(callbackService) && args(callbackDto)',
    argNames='callbackDto'
  )
  public void create( CallbackDto callbackDto ) {}

  @Before('create(callbackDto)')
  void before( CallbackDto callbackDto ) {
    log.info( "<< callbackDto: {}", callbackDto )
  }

  @AfterReturning(
    pointcut='create(mx.finerio.api.dtos.CallbackDto)',
    returning='response'
  )
  void afterReturning( Callback response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(mx.finerio.api.dtos.CallbackDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
