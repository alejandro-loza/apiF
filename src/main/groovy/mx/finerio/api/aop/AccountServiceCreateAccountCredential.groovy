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
class AccountServiceCreateAccountCredential {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountServiceCreateAccountCredential' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.AccountService.createAccountCredential(..)) && bean(accountService) && args(account, credential)',
    argNames='account, credential'
  )
  public void createAccountCredential( Account account, Credential credential) {}

  @Before('createAccountCredential(account, credential)')
  void before( Account account, Credential credential ) {
    log.info( "<< account: {}, credential: {}", account, credential )
  }

  @AfterReturning(
    pointcut='createAccountCredential(mx.finerio.api.domain.Account, mx.finerio.api.domain.Credential)',
    returning='response'
  )
  void afterReturning() {
    log.info( '>> Ok' )
  }

  @AfterThrowing(
    pointcut='createAccountCredential(mx.finerio.api.domain.Account, mx.finerio.api.domain.Credential)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
