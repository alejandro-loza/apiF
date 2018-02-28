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
class FinancialInstitutionServiceFindAll {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.FinancialInstitutionServiceFindAll' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.FinancialInstitutionService.findAll()) && bean(financialInstitutionService)'
  )
  public void findAll() {}

  @Before('findAll()')
  void before() {
    log.info( "<< OK" )
  }

  @AfterReturning(
    pointcut='findAll()',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findAll()',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
