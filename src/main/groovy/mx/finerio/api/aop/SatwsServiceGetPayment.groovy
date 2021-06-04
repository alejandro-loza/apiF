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
class SatwsServiceGetPayment {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceGetPayment' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsService.getPayment(..)) && bean(satwsService) && args(paymentId)',
    argNames='paymentId'
  )
  public void getPayment( String paymentId  ) {}

  @Before('getPayment(paymentId)')
  void before( String paymentId  ) {
    log.info( "<< paymentId: {}", paymentId  )
  }

  @AfterReturning(
    pointcut='getPayment(java.lang.String)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getPayment(java.lang.String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
