package mx.finerio.api.aop

import mx.finerio.api.dtos.FailureCallbackDto

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
class ScraperCallbackServiceProcessFailure {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperCallbackServiceProcessFailure' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.ScraperCallbackService.processFailure(..)) && bean(scraperCallbackService) && args(failureCallbackDto)',
    argNames='failureCallbackDto'
  )
  public void processFailure( FailureCallbackDto failureCallbackDto ) {}

  @Before('processFailure(failureCallbackDto)')
  void before( FailureCallbackDto failureCallbackDto ) {
    log.info( "<< failureCallbackDto: {}", failureCallbackDto )
  }

  @AfterReturning(
    pointcut='processFailure(mx.finerio.api.dtos.FailureCallbackDto)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='processFailure(mx.finerio.api.dtos.FailureCallbackDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
