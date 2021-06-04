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
class SatwsServiceDeleteTaxComplianceCheck {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceDeleteTaxComplianceCheck' )

  @Pointcut(
    value='execution(java.lang.String mx.finerio.api.services.SatwsService.deleteTaxComplianceCheck(..)) && bean(satwsService) && args(taxComplianceCheckId)',
    argNames='taxComplianceCheckId'
  )
  public void deleteTaxComplianceCheck( String taxComplianceCheckId  ) {}

  @Before('deleteTaxComplianceCheck(taxComplianceCheckId)')
  void before( String taxComplianceCheckId  ) {
    log.info( "<< taxComplianceCheckId: {}", taxComplianceCheckId  )
  }

  @AfterReturning(
    pointcut='deleteTaxComplianceCheck(java.lang.String)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='deleteTaxComplianceCheck(java.lang.String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
