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
class ScraperV2ClientServiceCreateCredentialLegacy{

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperV2ClientServiceCreateCredentialLegacy' )

  @Pointcut(
    value='execution(java.lang.String mx.finerio.api.services.ScraperV2ClientService.createCredentialLegacy(..)) && bean(scraperV2ClientService) && args(data)',
    argNames='data'
  )
  public void createCredentialLegacy( Map data ) {}

  @Before('createCredentialLegacy(data)')
  void before(Map data) {
    log.info( "<< data: {}", data )
  }

  @AfterReturning(
    pointcut='createCredentialLegacy(java.util.Map)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='createCredentialLegacy(java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
