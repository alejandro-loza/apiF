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
class ScraperCallbackServiceProcessMovements {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperCallbackServiceProcessMovements' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.ScraperCallbackService.processMovements(..)) && bean(scraperCallbackService) && args(movements,credentialId)',
    argNames='movements,credentialId'
  )
  public void processMovements( List movements, String credentialId ) {}

  @Before('processMovements(movements,credentialId)')
  void before( List movements, String credentialId ) {
    log.info( "<< movements: {}, credentialId: {}", movements, credentialId )
  }

  @AfterReturning(
    pointcut='processMovements(java.util.List,String)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='processMovements(java.util.List,String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
