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
class AccountServiceFindOne {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountServiceFindOne' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Account mx.finerio.api.services.AccountService.findOne(..)) && bean(accountService) && args(id)',
    argNames='id'
  )
  public void findOne( String id ) {}

  @Before('findOne(id)')
  void before( String id ) {
    log.info( "<< id: {}", id )
  }

  @AfterReturning(
    pointcut='findOne(String)',
    returning='response'
  )
  void afterReturning( Account response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOne(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
