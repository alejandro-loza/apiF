package mx.finerio.api.aop

import mx.finerio.api.dtos.SuccessCallbackDto

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import mx.finerio.api.domain.Credential

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component

@Component
@Aspect
class ScraperCallbackServiceProcessSuccess {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperCallbackServiceProcessSuccess' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Credential mx.finerio.api.services.ScraperCallbackService.processSuccess(..)) && bean(scraperCallbackService) && args(successCallbackDto)',
    argNames='successCallbackDto'
  )
  public void processSuccess( SuccessCallbackDto successCallbackDto ) {}

  @Before('processSuccess(successCallbackDto)')
  void before( SuccessCallbackDto successCallbackDto ) {
    log.info( "<< successCallbackDto: {}", successCallbackDto )
  }

  @AfterReturning(
    pointcut='processSuccess(mx.finerio.api.dtos.SuccessCallbackDto)',
    returning='response'
  )
  void afterReturning(Credential response) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='processSuccess(mx.finerio.api.dtos.SuccessCallbackDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
