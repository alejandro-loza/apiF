package mx.finerio.api.aop

import mx.finerio.api.domain.CreditDetails

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
class CreditDetailsServiceGetFields {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CreditDetailsServiceGetFields' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.CreditDetailsService.getFields(..)) && bean(creditDetailsService) && args(creditDetails)',
    argNames='creditDetails'
  )
  public void getFields( CreditDetails creditDetails ) {}

  @Before('getFields(creditDetails)')
  void before( CreditDetails creditDetails ) {
    log.info( "<< creditDetails: {}", creditDetails )
  }

  @AfterReturning(
    pointcut='getFields(mx.finerio.api.domain.CreditDetails)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getFields(mx.finerio.api.domain.CreditDetails)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
