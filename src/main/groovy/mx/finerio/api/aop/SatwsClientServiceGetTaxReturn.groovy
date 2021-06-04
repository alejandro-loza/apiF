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
class SatwsClientServiceGetTaxReturn {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsClientServiceGetTaxReturn' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsClientService.getTaxReturn(..)) && bean(satwsClientService) && args(taxReturnId)',
    argNames='taxReturnId'
  )
  public void getTaxReturn( String taxReturnId  ) {}

  @Before('getTaxReturn(taxReturnId)')
  void before( String taxReturnId  ) {
    log.info( "<< taxReturnId: {}", taxReturnId  )
  }

  @AfterReturning(
    pointcut='getTaxReturn(java.lang.String)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getTaxReturn(java.lang.String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
