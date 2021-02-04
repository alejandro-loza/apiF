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
class ScraperV2ClientServiceCreateCredential{

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperV2ClientServiceCreateCredential' )

  @Pointcut(
    value='execution(java.lang.String mx.finerio.api.services.ScraperV2ClientService.createCredential(..)) && bean(scraperV2ClientService) && args(data)',
    argNames='data'
  )
  public void createCredential( Map data ) {}

  @Before('createCredential(data)')
  void before(Map data) {
    log.info( "<< data: {}", data )
  }

  @AfterReturning(
    pointcut='createCredential(java.util.Map)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='createCredential(java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
