package mx.finerio.api.aop

import mx.finerio.api.domain.Movement
import mx.finerio.api.domain.Movement.Type

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
class MovementServiceGetMovementsToDuplicated {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.MovementServiceGetMovementsToDuplicated' )

  @Pointcut(
    value='execution(java.util.List mx.finerio.api.services.MovementService.getMovementsToDuplicated(..)) && bean(movementService) && args(movement)',
    argNames='movement'
  )
  public void getMovementsToDuplicated( Movement movement ) {}

  @Before('getMovementsToDuplicated(movement)')
  void before( Movement movement ) {
    log.info( "<< movement: {}", movement )
  }

  @AfterReturning(
    pointcut='getMovementsToDuplicated(mx.finerio.api.domain.Movement)',
    returning='response'
  )
  void afterReturning( List response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getMovementsToDuplicated(mx.finerio.api.domain.Movement)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
