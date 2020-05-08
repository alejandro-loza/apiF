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
class TransactionServiceDeleteAllByAccount {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.TransactionServiceDeleteAllByAccount' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.TransactionService.deleteAllByAccount(..)) && bean(transactionService) && args(account)',
    argNames='account'
  )
  public void deleteAllByAccount( Account account ) {}

  @Before('deleteAllByAccount(account)')
  void before( Account account ) {
    log.info( "<< account: {}", account )
  }

  @AfterReturning(
    pointcut='deleteAllByAccount(mx.finerio.api.domain.Account)'
  )
  void afterReturning() {
    log.info( '>> response: OK' )
  }

  @AfterThrowing(
    pointcut='deleteAllByAccount(mx.finerio.api.domain.Account)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
