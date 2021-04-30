package mx.finerio.api.aop

import mx.finerio.api.domain.Credential

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
class SatwsClientServiceGetTaxpayerInvoicePayments {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsClientServiceGetTaxpayerInvoicePayments' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsClientService.getTaxpayerInvoicePayments(..)) && bean(satwsClientService) && args(taxPayerId,params)',
    argNames='taxPayerId,params'
  )
  public void getTaxpayerInvoicePayments( String taxPayerId, Map params ) {}

  @Before('getTaxpayerInvoicePayments(taxPayerId,params)')
  void before( String taxPayerId, Map params ) {
    log.info( "<< taxPayerId: {}, params: {}", taxPayerId, params )
  }

  @AfterReturning(
    pointcut='getTaxpayerInvoicePayments(java.lang.String,java.util.Map)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getTaxpayerInvoicePayments(java.lang.String,java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
