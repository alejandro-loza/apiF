package mx.finerio.api.aop

import mx.finerio.api.domain.Movement

import org.aspectj.lang.annotation.*

import org.slf4j.*

import org.springframework.stereotype.Component

@Component
@Aspect
class TransactionPostProcessorServiceProcessDuplicated {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.TransactionPostProcessorServiceProcessDuplicated' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.TransactionPostProcessorService.processDuplicated(..)) && bean(transactionPostProcessorService) && args(movement)',
    argNames='movement'
  )
  public void processDuplicated( Movement movement ) {}

  @Before('processDuplicated(movement)')
  void before( Movement movement ) {
    log.info( "<< movement: {}", movement )
  }

  @AfterReturning(
    pointcut='processDuplicated(mx.finerio.api.domain.Movement)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='processDuplicated(mx.finerio.api.domain.Movement)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
