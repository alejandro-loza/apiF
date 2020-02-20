package mx.finerio.api.aop

import mx.finerio.api.domain.CreditDetails

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
class CreditDetailsServiceFindByAccountId {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CreditDetailsServiceFindByAccountId' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.CreditDetails mx.finerio.api.services.CreditDetailsService.findByAccountId(..)) && bean(creditDetailsService) && args(id)',
    argNames='id'
  )
  public void findByAccountId( String id ) {}

  @Before('findByAccountId(id)')
  void before( String id ) {
    log.info( "<< id: {}", id )
  }

  @AfterReturning(
    pointcut='findByAccountId(String)',
    returning='response'
  )
  void afterReturning( CreditDetails response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findByAccountId(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
