package mx.finerio.api.aop

import mx.finerio.api.domain.Transaction

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
class TransactionServiceFindOne {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.TransactionServiceFindOne' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Transaction mx.finerio.api.services.TransactionService.findOne(..)) && bean(transactionService) && args(id)',
    argNames='id'
  )
  public void findOne( Long id ) {}

  @Before('findOne(id)')
  void before( Long id ) {
    log.info( "<< id: {}", id )
  }

  @AfterReturning(
    pointcut='findOne(Long)',
    returning='response'
  )
  void afterReturning( Transaction response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOne(Long)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
