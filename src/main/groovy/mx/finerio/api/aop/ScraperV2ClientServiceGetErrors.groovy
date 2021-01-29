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
class ScraperV2ClientServiceGetErrors {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperV2ClientServiceGetErrors' )

  @Pointcut(
    value='execution(java.util.List mx.finerio.api.services.ScraperV2ClientService.getErrors()) && bean(scraperV2ClientService)'
  )
  public void getErrors() {}

  @Before('getErrors()')
  void before() {
    log.info( "<< OK",  )
  }

  @AfterReturning(
    pointcut='getErrors()',
    returning='response'
  )
  void afterReturning( List response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getErrors()',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
