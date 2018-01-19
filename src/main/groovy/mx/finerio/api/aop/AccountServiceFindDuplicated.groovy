package mx.finerio.api.aop

import mx.finerio.api.domain.*

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
class AccountServiceFindDuplicated {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountServiceFindDuplicated' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Account mx.finerio.api.services.AccountService.findDuplicated(..)) && bean(accountService) && args(institution, user, number, name)',
    argNames='institution, user, number, name'
  )
  public void findDuplicated( FinancialInstitution institution, User user, String number, String name ) {}

  @Before('findDuplicated(institution, user, number, name)')
  void before( FinancialInstitution institution, User user, String number, String name ) {
    log.info( "<< institution: {}, user: {}, number: {}, name: {}", institution, user, number, name )
  }

  @AfterReturning(
    pointcut='findDuplicated(mx.finerio.api.domain.FinancialInstitution, mx.finerio.api.domain.User, String, String)',
    returning='response'
  )
  void afterReturning( Account response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findDuplicated(mx.finerio.api.domain.FinancialInstitution, mx.finerio.api.domain.User, String, String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
