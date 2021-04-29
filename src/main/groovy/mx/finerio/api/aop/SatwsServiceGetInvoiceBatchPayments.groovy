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
class SatwsServiceGetInvoiceBatchPayments {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceGetInvoiceBatchPayments' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsClientService.getInvoiceBatchPayments(..)) && bean(satwsClientService) && args(invoiceId,params)',
    argNames='invoiceId,params'
  )
  public void getInvoiceBatchPayments( String invoiceId, Map params ) {}

  @Before('getInvoiceBatchPayments(invoiceId,params)')
  void before( String invoiceId, Map params ) {
    log.info( "<< invoiceId: {}, params: {}", invoiceId, params )
  }

  @AfterReturning(
    pointcut='getInvoiceBatchPayments(java.lang.String,java.util.Map)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getInvoiceBatchPayments(java.lang.String,java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
