package mx.finerio.api.aop

import mx.finerio.api.domain.Account
import mx.finerio.api.dtos.AccountData

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
class AccountServiceCreate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountServiceCreate' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Account mx.finerio.api.services.AccountService.create(..)) && bean(accountService) && args(accountData)',
    argNames='accountData'
  )
  public void create( AccountData accountData ) {}

  @Before('create(accountData)')
  void before( AccountData accountData ) {
    log.info( "<< accountData: {}", accountData )
  }

  @AfterReturning(
    pointcut='create(mx.finerio.api.dtos.AccountData)',
    returning='response'
  )
  void afterReturning( Account response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(mx.finerio.api.dtos.AccountData)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
