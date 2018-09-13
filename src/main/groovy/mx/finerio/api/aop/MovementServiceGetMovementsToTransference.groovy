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
class MovementServiceGetMovementsToTransference {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.MovementServiceGetMovementsToTransference' )

  @Pointcut(
    value='execution(java.util.List mx.finerio.api.services.MovementService.getMovementsToTransference(..)) && bean(movementService) && args(movement, type)',
    argNames='movement, type'
  )
  public void getMovementsToTransference( Movement movement, Type type ) {}

  @Before('getMovementsToTransference(movement, type)')
  void before( Movement movement, Type type ) {
    log.info( "<< movement: {}, type: {}", movement, type )
  }

  @AfterReturning(
    pointcut='getMovementsToTransference(mx.finerio.api.domain.Movement, mx.finerio.api.domain.Movement.Type)',
    returning='response'
  )
  void afterReturning( List response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getMovementsToTransference(mx.finerio.api.domain.Movement, mx.finerio.api.domain.Movement.Type)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
