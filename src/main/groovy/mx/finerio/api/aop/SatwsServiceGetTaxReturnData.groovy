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
class SatwsServiceGetTaxReturnData {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceGetTaxReturnData' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsService.getTaxReturnData(..)) && bean(satwsService) && args(taxReturnId)',
    argNames='taxReturnId'
  )
  public void getTaxReturnData( String taxReturnId  ) {}

  @Before('getTaxReturnData(taxReturnId)')
  void before( String taxReturnId  ) {
    log.info( "<< taxReturnId: {}", taxReturnId  )
  }

  @AfterReturning(
    pointcut='getTaxReturnData(java.lang.String)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getTaxReturnData(java.lang.String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
