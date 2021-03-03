package mx.finerio.api.aop

import mx.finerio.api.dtos.SummaryDto

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
class SummaryServiceGetSummaryByCustomer {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SummaryServiceGetSummaryByCustomer' )

  @Pointcut(
    value='execution(mx.finerio.api.dtos.SummaryDto mx.finerio.api.services.SummaryService.getSummaryByCustomer(..)) && bean(summaryService) && args(customerId)',
    argNames='customerId'
  )
  public void getSummaryByCustomer( Long customerId ) {}

  @Before('getSummaryByCustomer(customerId)')
  void before( Long customerId ) {
    log.info( "<< customerId: {}", customerId )
  }

  @AfterReturning(
    pointcut='getSummaryByCustomer(Long)',
    returning='response'
  )
  void afterReturning( SummaryDto response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getSummaryByCustomer(Long)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
