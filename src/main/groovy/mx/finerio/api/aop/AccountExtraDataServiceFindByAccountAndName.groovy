package mx.finerio.api.aop

import mx.finerio.api.dtos.AccountExtraDataDto

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
class AccountExtraDataServiceFindByAccountAndName {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountExtraDataServiceFindByAccountAndName' )

  @Pointcut(
    value='execution(mx.finerio.api.dtos.AccountExtraDataDto mx.finerio.api.services.AccountExtraDataService.findByAccountAndName(..)) && bean(accountExtraDataService) && args(accountId, name)',
    argNames='accountId, name'
  )
  public void findByAccountAndName( String accountId, String name ) {}

  @Before('findByAccountAndName(accountId, name)')
  void before( String accountId, String name ) {
    log.info( "<< accountId: {}, name: {}", accountId, name )
  }

  @AfterReturning(
    pointcut='findByAccountAndName(String, String)',
    returning='response'
  )
  void afterReturning( AccountExtraDataDto response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findByAccountAndName(String, String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
