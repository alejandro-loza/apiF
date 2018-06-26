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
    value='execution(mx.finerio.api.domain.Movement mx.finerio.api.services.TransactionPostProcessorService.processDuplicated(..)) && bean(transactionPostProcessorService) && args(movement)',
    argNames='movement'
  )
  public void processDuplicated( Movement id ) {}

  @Before('processDuplicated(movement)')
  void before( Movement movement ) {
    log.info( "<< movement: {}", movement )
  }

  @AfterReturning(
    pointcut='processDuplicated(mx.finerio.api.domain.Movement)',
    returning='response'
  )
  void afterReturning( Movement response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='processDuplicated(mx.finerio.api.domain.Movement)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
