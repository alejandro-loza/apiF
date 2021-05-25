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
class SatwsServiceGetTaxComplianceCheck {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceGetTaxComplianceCheck' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsService.getTaxComplianceCheck(..)) && bean(satwsService) && args(taxComplianceCheckId)',
    argNames='taxComplianceCheckId'
  )
  public void getTaxComplianceCheck( String taxComplianceCheckId  ) {}

  @Before('getTaxComplianceCheck(taxComplianceCheckId)')
  void before( String taxComplianceCheckId  ) {
    log.info( "<< taxComplianceCheckId: {}", taxComplianceCheckId  )
  }

  @AfterReturning(
    pointcut='getTaxComplianceCheck(java.lang.String)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getTaxComplianceCheck(java.lang.String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
