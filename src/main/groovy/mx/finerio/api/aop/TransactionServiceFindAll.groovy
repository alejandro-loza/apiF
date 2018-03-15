package mx.finerio.api.aop

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
class TransactionServiceFindAll {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.TransactionServiceFindAll' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.TransactionService.findAll(..)) && bean(transactionService) && args(params)',
    argNames='params'
  )
  public void findAll( Map params ) {}

  @Before('findAll(params)')
  void before( Map params ) {
    log.info( "<< params: {}", params )
  }

  @AfterReturning(
    pointcut='findAll(java.util.Map)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findAll(java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
