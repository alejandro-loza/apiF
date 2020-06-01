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
class AccountDetailsServiceFindAllByAccount {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountDetailsServiceFindAllByAccount' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.AccountDetailsService.findAllByAccount(..)) && bean(accountDetailsService) && args(id)',
    argNames='id'
  )
  public void findAllByAccount( String id ) {}

  @Before('findAllByAccount(id)')
  void before( String id ) {
    log.info( "<< id: {}", id )
  }

  @AfterReturning(
    pointcut='findAllByAccount(String)',
    returning='response'
  )
  void afterReturning( Map response ) {
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
