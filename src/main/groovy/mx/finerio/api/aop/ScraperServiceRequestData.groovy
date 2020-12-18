package mx.finerio.api.aop

import mx.finerio.api.domain.Credential

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
class ScraperServiceRequestData {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperServiceRequestData' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.DevScraperService.requestData(..)) && bean(devScraperService) && args(params)',
    argNames='params'
  )
  public void requestData( Map params ) {}

  @Before('requestData(params)')
  void before( Map params ) {
    log.info( "<< params: {}", params )
  }

  @AfterReturning(
    pointcut='requestData(java.util.Map)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='requestData(java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.DevScraperService.requestData(..)) && bean(devScraperService) && args(credential)',
    argNames='credential'
  )
  public void requestData2( Credential credential ) {}

  @Before('requestData2(credential)')
  void before2( Credential credential ) {
    log.info( "<< credential: {}", credential )
  }

  @AfterReturning(
    pointcut='requestData2(mx.finerio.api.domain.Credential)',
    returning='response'
  )
  void afterReturning2( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='requestData2(mx.finerio.api.domain.Credential)',
    throwing='e'
  )
  void afterThrowing2( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
