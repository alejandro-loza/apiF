package mx.finerio.api.aop

import mx.finerio.api.domain.CreditDetails
import mx.finerio.api.domain.Account
import mx.finerio.api.dtos.CreditDetailsDto

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
class CreditDetailsServiceCreate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CreditDetailsServiceCreate' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.CreditDetails mx.finerio.api.services.CreditDetailsService.create(..)) && bean(creditDetailsService) && args(creditDetailsDto,account)',
    argNames='creditDetailsDto, account'
  )
  public void create( CreditDetailsDto creditDetailsDto, Account account ) {}

  @Before('create(creditDetailsDto,account)')
  void before( CreditDetailsDto creditDetailsDto,Account account ) {
    log.info( "<< creditDetailsDto: {}, account: {}", creditDetailsDto, account )
  }

  @AfterReturning(
    pointcut='create(mx.finerio.api.dtos.CreditDetailsDto, mx.finerio.api.domain.Account)',
    returning='response'
  )
  void afterReturning( CreditDetails response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(mx.finerio.api.dtos.CreditDetailsDto, mx.finerio.api.domain.Account)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
