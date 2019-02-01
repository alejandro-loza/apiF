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
    value='execution(void mx.finerio.api.services.ScraperCallbackService.processMovements(..)) && bean(scraperCallbackService) && args(movements)',
    argNames='movements'
  )
  public void processMovements( List movements ) {}

  @Before('processMovements(movements)')
  void before( List movements ) {
    log.info( "<< movements: {}", movements )
  }

  @AfterReturning(
    pointcut='processMovements(java.util.List)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='processMovements(java.util.List)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
