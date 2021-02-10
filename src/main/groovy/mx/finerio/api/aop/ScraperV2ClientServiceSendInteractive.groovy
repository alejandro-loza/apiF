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
class ScraperV2ClientServiceSendInteractive{

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperV2ClientServiceSendInteractive' )

  @Pointcut(
    value='execution(java.lang.String mx.finerio.api.services.ScraperV2ClientService.sendInteractive(..)) && bean(scraperV2ClientService) && args(data)',
    argNames='data'
  )
  public void sendInteractive( Map data ) {}

  @Before('sendInteractive(data)')
  void before(Map data) {
    log.info( "<< data: {}", data )
  }

  @AfterReturning(
    pointcut='sendInteractive(java.util.Map)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='sendInteractive(java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
