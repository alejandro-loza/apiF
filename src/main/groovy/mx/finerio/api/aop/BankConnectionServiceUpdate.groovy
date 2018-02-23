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
class BankConnectionServiceUpdate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.BankConnectionServiceUpdate' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.BankConnection mx.finerio.api.services.BankConnectionService.update(..)) && bean(bankConnectionService) && args(credential, status)',
    argNames='credential, status'
  )
  public void update( Credential credential, BankConnection.Status status ) {}

  @Before('update(credential, status)')
  void before( Credential credential, BankConnection.Status status ) {
    log.info( "<< credential: {}, status: {}", credential, status )
  }

  @AfterReturning(
    pointcut='update(mx.finerio.api.domain.Credential, mx.finerio.api.domain.BankConnection.Status)',
    returning='response'
  )
  void afterReturning( BankConnection response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='update(mx.finerio.api.domain.Credential, mx.finerio.api.domain.BankConnection.Status)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
