package mx.finerio.api.aop

import mx.finerio.api.dtos.ScraperWebSocketSendDto

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
    value='execution(void mx.finerio.api.services.ScraperWebSocketService.send(..)) && args(scraperWebSocketSendDto)',
    argNames='scraperWebSocketSendDto'
  )
  public void send( ScraperWebSocketSendDto scraperWebSocketSendDto ) {}

  @Before('send(scraperWebSocketSendDto)')
  void before( ScraperWebSocketSendDto scraperWebSocketSendDto ) {
    log.info( "<< scraperWebSocketSendDto: {}", scraperWebSocketSendDto )
  }

  @AfterReturning(
    pointcut='send(mx.finerio.api.dtos.ScraperWebSocketSendDto)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='send(mx.finerio.api.dtos.ScraperWebSocketSendDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
