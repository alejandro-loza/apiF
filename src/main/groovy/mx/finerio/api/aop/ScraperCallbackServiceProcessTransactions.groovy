package mx.finerio.api.aop

import mx.finerio.api.dtos.TransactionDto

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
class ScraperCallbackServiceProcessTransactions {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperCallbackServiceProcessTransactions' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.ScraperCallbackService.processTransactions(..)) && bean(scraperCallbackService) && args(transactionDto)',
    argNames='transactionDto'
  )
  public void processTransactions( TransactionDto transactionDto ) {}

  @Before('processTransactions(transactionDto)')
  void before( TransactionDto transactionDto ) {
    log.info( "<< transactionDto: {}", transactionDto )
  }

  @AfterReturning(
    pointcut='processTransactions(mx.finerio.api.dtos.TransactionDto)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='processTransactions(mx.finerio.api.dtos.TransactionDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
