package mx.finerio.api.aop

import mx.finerio.api.domain.Movement

import org.aspectj.lang.annotation.*

import org.slf4j.*

import org.springframework.stereotype.Component

@Component
@Aspect
class TransactionPostProcessorServiceUpdateTransference {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.TransactionPostProcessorServiceUpdateTransference' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.TransactionPostProcessorService.updateTransference(..)) && bean(transactionPostProcessorService) && args(movement)',
    argNames='movement'
  )
  public void updateTransference( Movement movement ) {}

  @Before('updateTransference(movement)')
  void before( Movement movement ) {
    log.info( "<< movement: {}", movement )
  }

  @AfterReturning(
    pointcut='updateTransference(mx.finerio.api.domain.Movement)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='updateTransference(mx.finerio.api.domain.Movement)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
