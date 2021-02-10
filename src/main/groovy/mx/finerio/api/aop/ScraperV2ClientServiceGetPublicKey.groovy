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
class ScraperV2ClientServiceGetPublicKey {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperV2ClientServiceGetPublicKey' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.ScraperV2ClientService.getPublicKey()) && bean(scraperV2ClientService)'
  )
  public void getPublicKey() {}

  @Before('getPublicKey()')
  void before() {
    log.info( "<< OK",  )
  }

  @AfterReturning(
    pointcut='getPublicKey()',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getPublicKey()',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
