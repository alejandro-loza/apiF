package mx.finerio.api.aop

import mx.finerio.api.domain.Movement

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
class MovementServiceGetFields {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.MovementServiceGetFields' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.MovementService.getFields(..)) && bean(movementService) && args(movement)',
    argNames='movement'
  )
  public void getFields( Movement movement ) {}

  @Before('getFields(movement)')
  void before( Movement movement ) {
    log.info( "<< movement: {}", movement )
  }

  @AfterReturning(
    pointcut='getFields(mx.finerio.api.domain.Movement)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getFields(mx.finerio.api.domain.Movement)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
