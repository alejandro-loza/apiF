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
class SatwsServiceGetInvoice {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceGetInvoice' )

  @Pointcut(
    value='execution(java.lang.Object mx.finerio.api.services.SatwsService.getInvoice(..)) && bean(satwsService) && args(invoiceId,accept,params)',
    argNames='invoiceId,accept,params'
  )
  public void getInvoice( String invoiceId, String accept, Map params ) {}

  @Before('getInvoice(invoiceId,accept,params)')
  void before( String invoiceId, String accept, Map params ) {
    log.info( "<< invoiceId: {}, accept: {} params: {}", invoiceId, accept, params )
  }

  @AfterReturning(
    pointcut='getInvoice(java.lang.String,java.lang.String,java.util.Map)',
    returning='response'
  )
  void afterReturning( Object response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getInvoice(java.lang.String,java.lang.String,java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
