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
class TransactionServiceCategorize {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.TransactionServiceCategorize' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.TransactionService.categorize(..)) && bean(transactionService) && args(transaction)',
    argNames='transaction'
  )
  public void categorize( Transaction transaction ) {}

  @Before('categorize(transaction)')
  void before( Transaction transaction ) {
    log.info( "<< transaction: {}", transaction)
  }

  @AfterReturning(
    pointcut='categorize(mx.finerio.api.domain.Transaction)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='categorize(mx.finerio.api.domain.Transaction)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
