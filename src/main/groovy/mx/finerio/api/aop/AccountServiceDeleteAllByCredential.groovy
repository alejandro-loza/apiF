package mx.finerio.api.aop

import mx.finerio.api.domain.Credential

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
class AccountServiceDeleteAllByCredential {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountServiceDeleteAllByCredential' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.AccountService.deleteAllByCredential(..)) && bean(accountService) && args(credential)',
    argNames='credential'
  )
  public void deleteAllByCredential( Credential credential ) {}

  @Before('deleteAllByCredential(credential)')
  void before( Credential credential ) {
    log.info( "<< credential: {}", credential )
  }

  @AfterReturning(
    pointcut='deleteAllByCredential(mx.finerio.api.domain.Credential)'
  )
  void afterReturning() {
    log.info( '>> response: OK' )
  }

  @AfterThrowing(
    pointcut='deleteAllByCredential(mx.finerio.api.domain.Credential)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
