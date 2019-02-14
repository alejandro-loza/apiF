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
class ScraperCallbackServicePostProcessSuccess {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperCallbackServicePostProcessSuccess' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.ScraperCallbackService.postProcessSuccess(..)) && bean(scraperCallbackService) && args(credential)',
    argNames='credential'
  )
  public void postProcessSuccess( Credential credential ) {}

  @Before('postProcessSuccess(credential)')
  void before( Credential credential ) {
    log.info( "<< credential: {}", credential )
  }

  @AfterReturning(
    pointcut='postProcessSuccess(mx.finerio.api.domain.Credential)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='postProcessSuccess(mx.finerio.api.domain.Credential)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
