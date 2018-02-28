package mx.finerio.api.aop

import mx.finerio.api.domain.FinancialInstitution

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
class FinancialInstitutionServiceGetFields {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.FinancialInstitutionServiceGetFields' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.FinancialInstitutionService.getFields(..)) && bean(financialInstitutionService) && args(financialInstitution)',
    argNames='financialInstitution'
  )
  public void getFields( FinancialInstitution financialInstitution ) {}

  @Before('getFields(financialInstitution)')
  void before( FinancialInstitution financialInstitution ) {
    log.info( "<< financialInstitution: {}", financialInstitution )
  }

  @AfterReturning(
    pointcut='getFields(mx.finerio.api.domain.FinancialInstitution)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getFields(mx.finerio.api.domain.FinancialInstitution)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
