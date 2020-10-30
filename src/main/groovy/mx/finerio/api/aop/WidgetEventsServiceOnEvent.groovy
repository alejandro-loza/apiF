package mx.finerio.api.aop

import mx.finerio.api.dtos.WidgetEventsDto

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
class WidgetEventsServiceOnEvent {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.WidgetEventsServiceOnEvent' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.WidgetEventsService.*(..)) && bean(widgetEventsService) && args(dto)',
    argNames='dto'
  )
  public void onEvent( WidgetEventsDto dto ) {}

  @Before('onEvent(dto)')
  void before(WidgetEventsDto dto ) {
    log.info( "<< dto: {}", dto )
  }

  @AfterReturning(
    pointcut='onEvent(mx.finerio.api.dtos.WidgetEventsDto)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='onEvent(mx.finerio.api.dtos.WidgetEventsDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
