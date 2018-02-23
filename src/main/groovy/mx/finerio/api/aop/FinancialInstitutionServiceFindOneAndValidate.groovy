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
class FinancialInstitutionServiceFindOneAndValidate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.FinancialInstitutionServiceFindOneAndValidate' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.FinancialInstitution mx.finerio.api.services.FinancialInstitutionService.findOneAndValidate(..)) && bean(financialInstitutionService) && args(id)',
    argNames='id'
  )
  public void findOneAndValidate( Long id ) {}

  @Before('findOneAndValidate(id)')
  void before( Long id ) {
    log.info( "<< id: {}", id )
  }

  @AfterReturning(
    pointcut='findOneAndValidate(Long)',
    returning='response'
  )
  void afterReturning( FinancialInstitution response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOneAndValidate(Long)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
