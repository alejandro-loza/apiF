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
class ConceptServiceUpdateAmounts {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ConceptServiceUpdateAmounts' )

  @Pointcut(
    value='execution(Object mx.finerio.api.services.ConceptService.updateAmounts(..)) && bean(conceptService) && args(concept)',
    argNames='concept'
  )
  public void updateAmounts(Concept concept ) {}

  @Before('updateAmounts(concept)')
  void before(Concept concept ) {
    log.info( "<< concept: {}", concept )
  }

  @AfterReturning(
    pointcut='updateAmounts(mx.finerio.api.domain.Concept)',
    returning='response'
  )
  void afterReturning( Object response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='updateAmounts(mx.finerio.api.domain.Concept)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
