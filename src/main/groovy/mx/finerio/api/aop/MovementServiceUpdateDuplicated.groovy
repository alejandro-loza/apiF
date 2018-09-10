
package mx.finerio.api.aop

import mx.finerio.api.domain.Movement

import org.aspectj.lang.annotation.*

import org.slf4j.*

import org.springframework.stereotype.Component

@Component
@Aspect
class MovementServiceUpdateDuplicated {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.MovementServiceUpdateDuplicated' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.MovementService.updateDuplicated(..)) && bean(movementService) && args(movement)',
    argNames='movement'
  )
  public void updateDuplicated( Movement movement ) {}

  @Before('updateDuplicated(movement)')
  void before( Movement movement ) {
    log.info( "<< movement: {}", movement )
  }

  @AfterReturning(
    pointcut='updateDuplicated(mx.finerio.api.domain.Movement)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='updateDuplicated(mx.finerio.api.domain.Movement)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
