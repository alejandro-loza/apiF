package mx.finerio.api.aop

import mx.finerio.api.domain.*

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
class ConceptServiceCreate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ConceptServiceCreate' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Concept mx.finerio.api.services.ConceptService.create(..)) && bean(conceptService) && args(movement, attributes)',
    argNames='movement, attributes'
  )
  public void create(Map movement, attributes ) {}

  @Before('create(movement, attributes)')
  void before(Map movement, attributes ) {
    log.info( "<< movement: {}, attributes: {}", movement, attributes )
  }

  @AfterReturning(
    pointcut='create(Movement, java.util.Map)',
    returning='response'
  )
  void afterReturning( Concept response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(Movement, java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
