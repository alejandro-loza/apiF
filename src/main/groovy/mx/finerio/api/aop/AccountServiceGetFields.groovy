package mx.finerio.api.aop

import mx.finerio.api.domain.Account

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
class AccountServiceGetFields {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountServiceGetFields' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.AccountService.getFields(..)) && bean(accountService) && args(account)',
    argNames='account'
  )
  public void getFields( Account account ) {}

  @Before('getFields(account)')
  void before( Account account ) {
    log.info( "<< account: {}", account )
  }

  @AfterReturning(
    pointcut='getFields(mx.finerio.api.domain.Account)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getFields(mx.finerio.api.domain.Account)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
