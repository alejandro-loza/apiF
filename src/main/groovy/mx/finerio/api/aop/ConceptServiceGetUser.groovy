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
class ConceptServiceGetUser {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ConceptServiceGetUser' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.User mx.finerio.api.services.ConceptService.getUser(..)) && bean(conceptService) && args(mov)',
    argNames='mov'
  )
  public void getUser(Movement mov ) {}

  @Before('getUser(mov)')
  void before(Movement mov ) {
    log.info( "<< mov: {}", mov )
  }

  @AfterReturning(
    pointcut='getUser(mx.finerio.api.domain.Movement)',
    returning='response'
  )
  void afterReturning( User response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getUser(mx.finerio.api.domain.Movement)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
