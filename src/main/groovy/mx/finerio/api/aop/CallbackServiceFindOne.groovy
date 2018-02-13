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
class CallbackServiceFindOne {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CallbackServiceFindOne' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Callback mx.finerio.api.services.CallbackService.findOne(..)) && bean(callbackService) && args(id)',
    argNames='id'
  )
  public void findOne( Long id ) {}

  @Before('findOne(id)')
  void before( Long id ) {
    log.info( "<< id: {}", id )
  }

  @AfterReturning(
    pointcut='findOne(Long)',
    returning='response'
  )
  void afterReturning( Callback response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOne(Long)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
