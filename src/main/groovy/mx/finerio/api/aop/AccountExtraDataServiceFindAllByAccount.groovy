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
class AccountExtraDataServiceFindAllByAccount {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountExtraDataServiceFindAllByAccount' )

  @Pointcut(
    value='execution(java.util.List mx.finerio.api.services.AccountExtraDataService.findAllByAccount(..)) && bean(accountExtraDataService) && args(accountId)',
    argNames='accountId'
  )
  public void findAllByAccount( String accountId ) {}

  @Before('findAllByAccount(accountId)')
  void before( String accountId ) {
    log.info( "<< accountId: {}", accountId )
  }

  @AfterReturning(
    pointcut='findAllByAccount(String)',
    returning='response'
  )
  void afterReturning( AccountExtraDataDto response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findAllByAccount(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
