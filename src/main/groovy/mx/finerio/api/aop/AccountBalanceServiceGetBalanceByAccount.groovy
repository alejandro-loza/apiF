package mx.finerio.api.aop

import mx.finerio.api.dtos.BalanceDto

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
class AccountBalanceServiceGetBalanceByAccount {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountBalanceServiceGetBalanceByAccount' )

  @Pointcut(
    value='execution(mx.finerio.api.dtos.BalanceDto mx.finerio.api.services.AccountBalanceService.getBalanceByAccount(..)) && bean(accountBalanceService) && args(accountId)',
    argNames='accountId'
  )
  public void getBalanceByAccount( String accountId ) {}

  @Before('getBalanceByAccount(accountId)')
  void before( String accountId ) {
    log.info( "<< accountId: {}", accountId )
  }

  @AfterReturning(
    pointcut='getBalanceByAccount(String)',
    returning='response'
  )
  void afterReturning( BalanceDto response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getBalanceByAccount(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
