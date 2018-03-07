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
class ScraperWebSocketServiceSend {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperWebSocketServiceSend' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.ScraperWebSocketService.send(..)) && bean(scraperWebSocketService) && args(message)',
    argNames='message'
  )
  public void send( String message ) {}

  @Before('send(message)')
  void before( String message ) {
    log.info( "<< message: {}", message )
  }

  @AfterReturning(
    pointcut='send(String)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='send(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
