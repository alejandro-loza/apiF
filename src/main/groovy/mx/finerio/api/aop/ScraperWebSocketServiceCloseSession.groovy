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
class ScraperWebSocketServiceCloseSession {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperWebSocketServiceCloseSession' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.ScraperWebSocketService.closeSession(..)) && bean(scraperWebSocketService) && args(id)',
    argNames='id'
  )
  public void closeSession( String id ) {}

  @Before('closeSession(id)')
  void before( String id ) {
    log.info( "<< id: {}", id )
  }

  @AfterReturning(
    pointcut='closeSession(String)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='closeSession(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.id}" )
  }

}
