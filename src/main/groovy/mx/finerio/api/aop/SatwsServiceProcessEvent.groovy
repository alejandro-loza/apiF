package mx.finerio.api.aop

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import mx.finerio.api.dtos.SatwsEventDto

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component

@Component
@Aspect
class SatwsServiceProcessEvent {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceProcessEvent' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.SatwsService.processEvent(..)) && bean(satwsService) && args(satwsEventDto)',
    argNames='satwsEventDto'
  )
  public void processEvent( SatwsEventDto satwsEventDto ) {}

  @Before('processEvent(satwsEventDto)')
  void before( SatwsEventDto satwsEventDto ) {
    log.info( "<< satwsEventDto: {}", satwsEventDto )
  }

  @AfterReturning(
    pointcut='processEvent(mx.finerio.api.dtos.SatwsEventDto)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='processEvent(mx.finerio.api.dtos.SatwsEventDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
