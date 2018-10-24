package mx.finerio.api.aop

import mx.finerio.api.domain.*

import org.aspectj.lang.annotation.*

import org.slf4j.*

import org.springframework.stereotype.Component

@Component
@Aspect
class ConceptServiceFindByMovement {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ConceptServiceFindByMovement' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Concept mx.finerio.api.services.ConceptService.findByMovement(..)) && bean(conceptService) && args(movement)',
    argNames='movement'
  )
  public void findByMovement(Movement movement ) {}

  @Before('findByMovement(movement)')
  void before(Movement movement ) {
    log.info( "<< movement: {}", movement )
  }

  @AfterReturning(
    pointcut='findByMovement(mx.finerio.api.domain.Movement)',
    returning='response'
  )
  void afterReturning( Concept response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findByMovement(mx.finerio.api.domain.Movement)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
