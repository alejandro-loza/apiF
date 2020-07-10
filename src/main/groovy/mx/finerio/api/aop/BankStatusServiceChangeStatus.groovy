package mx.finerio.api.aop

import mx.finerio.api.dtos.BankStatusDto

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
class BankStatusServiceChangeStatus {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.BankStatusServiceChangeStatus' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.BankStatusService.changeStatus(..)) && bean(bankStatusService) && args(dto)',
    argNames='dto'
  )
  public void changeStatus( BankStatusDto dto ) {}

  @Before('changeStatus(dto)')
  void before( BankStatusDto dto ) {
    log.info( "<< dto: {}", dto )
  }

  @AfterReturning(
    pointcut='changeStatus(mx.finerio.api.dtos.BankStatusDto)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='changeStatus(mx.finerio.api.dtos.BankStatusDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
