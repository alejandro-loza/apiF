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
class SatwsServiceGetTaxpayersTaxStatus {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceGetTaxpayersTaxStatus' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsService.getTaxpayersTaxStatus(..)) && bean(satwsService) && args(customerId)',
    argNames='customerId'
  )
  public void getTaxpayersTaxStatus( Long customerId) {}

  @Before('getTaxpayersTaxStatus(customerId)')
  void before( Long customerId ) {
    log.info( "<< customerId: {}", customerId )
  }

  @AfterReturning(
    pointcut='getTaxpayersTaxStatus(java.lang.Long)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getTaxpayersTaxStatus(java.lang.Long)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
