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
class TransactionServiceGetFields {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.TransactionServiceGetFields' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.TransactionService.getFields(..)) && bean(transactionService) && args(transaction)',
    argNames='transaction'
  )
  public void getFields( Transaction transaction ) {}

  @Before('getFields(transaction)')
  void before( Transaction transaction ) {
    log.info( "<< transaction: {}", transaction )
  }

  @AfterReturning(
    pointcut='getFields(mx.finerio.api.domain.Transaction)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getFields(mx.finerio.api.domain.Transaction)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
