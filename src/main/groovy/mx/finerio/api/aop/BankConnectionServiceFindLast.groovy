package mx.finerio.api.aop

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.BankConnection

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
class BankConnectionServiceFindLast {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.BankConnectionServiceFindLast' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.BankConnection mx.finerio.api.services.BankConnectionService.findLast(..)) && bean(bankConnectionService) && args(credential)',
    argNames='credential'
  )
  public void findLast( Credential credential ) {}

  @Before('findLast(credential)')
  void before( Credential credential ) {
    log.info( "<< credential: {}", credential )
  }

  @AfterReturning(
    pointcut='findLast(mx.finerio.api.domain.Credential)',
    returning='response'
  )
  void afterReturning( BankConnection response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findLast(mx.finerio.api.domain.Credential)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
