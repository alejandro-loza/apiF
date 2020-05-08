
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
class BankFieldServiceFindAllByFinancialInstitution {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.BankFieldServiceFindAllByFinancialInstitution' )

  @Pointcut(
    value='execution(java.util.List mx.finerio.api.services.BankFieldService.findAllByFinancialInstitution(..)) && bean(bankFieldService) && args(id)',
    argNames='id'
  )
  public void findAllByFinancialInstitution( Long id ) {}

  @Before('findAllByFinancialInstitution(id)')
  void before( Long id ) {
    log.info( "<< id: {}", id )
  }

  @AfterReturning(
    pointcut='findAllByFinancialInstitution(Long)',
    returning='response'
  )
  void afterReturning( List response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findAllByFinancialInstitution(Long)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
