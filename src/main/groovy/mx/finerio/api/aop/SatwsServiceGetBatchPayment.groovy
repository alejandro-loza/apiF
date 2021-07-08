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
class SatwsServiceGetBatchPayment {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceGetBatchPayment' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsService.getBatchPayment(..)) && bean(satwsService) && args(batchPaymentId)',
    argNames='batchPaymentId'
  )
  public void getBatchPayment( String batchPaymentId  ) {}

  @Before('getBatchPayment(batchPaymentId)')
  void before( String batchPaymentId  ) {
    log.info( "<< batchPaymentId: {}", batchPaymentId  )
  }

  @AfterReturning(
    pointcut='getBatchPayment(java.lang.String)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getBatchPayment(java.lang.String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
