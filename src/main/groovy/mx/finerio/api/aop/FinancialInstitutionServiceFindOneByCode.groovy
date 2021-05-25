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
class FinancialInstitutionServiceFindOneByCode {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.FinancialInstitutionServiceFindOneByCode' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.FinancialInstitution mx.finerio.api.services.FinancialInstitutionService.findOneByCode(..)) && bean(financialInstitutionService) && args(code)',
    argNames='code'
  )
  public void findOneByCode( String code ) {}

  @Before('findOneByCode(code)')
  void before( String code ) {
    log.info( "<< code: {}", code )
  }

  @AfterReturning(
    pointcut='findOneByCode(String)',
    returning='response'
  )
  void afterReturning( FinancialInstitution response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOneByCode(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
