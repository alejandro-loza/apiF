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
class DuplicatedTransactionsValidatorServiceValidateTransactionsFromSameDate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.DuplicatedTransactionsValidatorServiceValidateTransactionsFromSameDate' )

  @Pointcut(
    value='execution(boolean mx.finerio.api.services.DuplicatedTransactionsValidatorService.validateTransactionsFromSameDate(..)) && bean(duplicatedTransactionsValidatorService) && args(transactionDto, transactionsToCompare)',
    argNames='transactionDto, transactionsToCompare'
  )
  public void validateTransactionsFromSameDate( DuplicatedTransactionDto transactionDto, List transactionsToCompare ) {}

  @Before('validateTransactionsFromSameDate(transactionDto, transactionsToCompare)')
  void before( DuplicatedTransactionDto transactionDto, List transactionsToCompare ) {
    log.info( "<< transactionDto: {}, transactionsToCompare: {}", transactionDto, transactionsToCompare )
  }

  @AfterReturning(
    pointcut='validateTransactionsFromSameDate(mx.finerio.api.dtos.DuplicatedTransactionDto, java.util.List)',
    returning='isDuplicated'
  )
  void afterReturning( boolean isDuplicated ) {
    log.info( '>> isDuplicated: {}', isDuplicated )
  }

  @AfterThrowing(
    pointcut='validateTransactionsFromSameDate(mx.finerio.api.dtos.DuplicatedTransactionDto, java.util.List)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
