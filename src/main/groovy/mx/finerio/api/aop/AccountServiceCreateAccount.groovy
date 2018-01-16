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
class AccountServiceCreateAccount {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountServiceCreateAccount' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Account mx.finerio.api.services.AccountService.createAccount(..)) && bean(AccountService) && args(params)',
    argNames='params'
  )
  public void createAccount(Map params ) {}

  @Before('createAccount(params)')
  void before(Map params ) {
    log.info( "<< params: {}", params )
  }

  @AfterReturning(
    pointcut='createAccount(java.util.Map)',
    returning='response'
  )
  void afterReturning( mx.finerio.api.domain.Account response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='createAccount(java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
