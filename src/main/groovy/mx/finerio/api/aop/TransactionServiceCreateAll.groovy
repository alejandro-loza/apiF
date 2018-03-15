package mx.finerio.api.aop

import mx.finerio.api.dtos.TransactionData

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
class TransactionServiceCreateAll {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.TransactionServiceCreateAll' )

  @Pointcut(
    value='execution(java.util.List mx.finerio.api.services.TransactionService.createAll(..)) && bean(transactionService) && args(transactionData)',
    argNames='transactionData'
  )
  public void createAll( TransactionData transactionData ) {}

  @Before('createAll(transactionData)')
  void before(TransactionData transactionData ) {
    log.info( "<< transactionData: {}", transactionData )
  }

  @AfterReturning(
    pointcut='createAll(mx.finerio.api.dtos.TransactionData)',
    returning='response'
  )
  void afterReturning( List response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='createAll(mx.finerio.api.dtos.TransactionData)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
