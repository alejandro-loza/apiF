package mx.finerio.api.aop

import mx.finerio.api.dtos.DuplicatedTransactionDto

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
class DuplicatedTransactionsValidatorServiceValidateTransactions {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.DuplicatedTransactionsValidatorServiceValidateTransactions' )

  @Pointcut(
    value='execution(boolean mx.finerio.api.services.DuplicatedTransactionsValidatorService.validateTransactions(..)) && bean(duplicatedTransactionsValidatorService) && args(transactionDto, transactionsToCompare)',
    argNames='transactionDto, transactionsToCompare'
  )
  public void validateTransactions( DuplicatedTransactionDto transactionDto, List transactionsToCompare ) {}

  @Before('validateTransactions(transactionDto, transactionsToCompare)')
  void before( DuplicatedTransactionDto transactionDto, List transactionsToCompare ) {
    log.info( "<< transactionDto: {}, transactionsToCompare: {}", transactionDto, transactionsToCompare )
  }

  @AfterReturning(
    pointcut='validateTransactions(mx.finerio.api.dtos.DuplicatedTransactionDto, java.util.List)',
    returning='isDuplicated'
  )
  void afterReturning( boolean isDuplicated ) {
    log.info( '>> isDuplicated: {}', isDuplicated )
  }

  @AfterThrowing(
    pointcut='validateTransactions(mx.finerio.api.dtos.DuplicatedTransactionDto, java.util.List)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
