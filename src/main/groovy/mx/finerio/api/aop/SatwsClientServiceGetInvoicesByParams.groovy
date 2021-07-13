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
class SatwsClientServiceGetInvoicesByParams {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsClientServiceGetInvoicesByParams' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsClientService.getInvoicesByParams(..)) && bean(satwsClientService) && args(rfc,params,customerId)',
    argNames='rfc,params,customerId'
  )
  public void getInvoicesByParams( String rfc, Map params, Long customerId ) {}

  @Before('getInvoicesByParams(rfc,params,customerId)')
  void before( String rfc, Map params, Long customerId ) {
    log.info( "<< rfc: {}, params: {} customerId:{}", rfc, params, customerId)
  }

  @AfterReturning(
    pointcut='getInvoicesByParams(java.lang.String,java.util.Map,java.lang.Long)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getInvoicesByParams(java.lang.String,java.util.Map,java.lang.Long)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
