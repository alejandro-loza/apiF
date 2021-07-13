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
class SatwsClientServiceGetInvoice {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsClientServiceGetInvoice' )

  @Pointcut(
    value='execution(java.lang.Object mx.finerio.api.services.SatwsClientService.getInvoice(..)) && bean(satwsClientService) && args(invoiceId,accept,customerId)',
    argNames='invoiceId,accept,customerId'
  )
  public void getInvoice( String invoiceId, String accept, Long customerId ) {}

  @Before('getInvoice(invoiceId,accept,customerId)')
  void before( String invoiceId, String accept, Long customerId ) {
    log.info( "<< invoiceId: {}, accept: {}, customerId: {}", invoiceId, accept, customerId)
  }

  @AfterReturning(
    pointcut='getInvoice(java.lang.String,java.lang.String,java.lang.Long)',
    returning='response'
  )
  void afterReturning( Object response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getInvoice(java.lang.String,java.lang.String,java.lang.Long)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
