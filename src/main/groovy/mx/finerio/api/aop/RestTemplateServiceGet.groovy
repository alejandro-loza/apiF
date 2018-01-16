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
class RestTemplateServiceGet {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.RestTemplateServiceGet' )

  @Pointcut(
    value='execution(Object mx.finerio.api.services.RestTemplateService.get(..)) && bean(restTemplateService) && args(url, headers, params)',
    argNames='url, headers, params'
  )
  public void getMethod( String url, Map headers, Map params ) {}

  @Before('getMethod(url, headers, params)')
  void before( String url, Map headers, Map params ) {
    log.info( "<< url: {}, headers: {}, params: {}", url, headers, params )
  }

  @AfterReturning(
    pointcut='getMethod(String, java.util.Map, java.util.Map)',
    returning='response'
  )
  void afterReturning( Object response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getMethod(String, java.util.Map, java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
