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
class MovementServiceCreateMovement {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.MovementServiceCreateMovement' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Movement mx.finerio.api.services.MovementService.createMovement(..)) && bean(movementService) && args(params)',
    argNames='params'
  )
  public void createMovement(Map params ) {}

  @Before('createMovement(params)')
  void before(Map params ) {
    log.info( "<< params: {}", params )
  }

  @AfterReturning(
    pointcut='createMovement(java.util.Map)',
    returning='response'
  )
  void afterReturning( Movement response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='createMovement(java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
