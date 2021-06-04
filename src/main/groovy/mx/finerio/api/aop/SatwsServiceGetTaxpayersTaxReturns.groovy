package mx.finerio.api.aop

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
class SatwsServiceGetTaxpayersTaxReturns {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceGetTaxpayersTaxReturns' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsService.getTaxpayersTaxReturns(..)) && bean(satwsService) && args(customerId,params)',
    argNames='customerId,params'
  )
  public void getTaxpayersTaxReturns( Long customerId, Map params ) {}

  @Before('getTaxpayersTaxReturns(customerId,params)')
  void before( Long customerId, Map params ) {
    log.info( "<< customerId: {}, params: {}", customerId, params )
  }

  @AfterReturning(
    pointcut='getTaxpayersTaxReturns(java.lang.Long,java.util.Map)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getTaxpayersTaxReturns(java.lang.Long,java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
