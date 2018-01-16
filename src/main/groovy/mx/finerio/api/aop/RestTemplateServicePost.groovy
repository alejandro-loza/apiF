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
class RestTemplateServicePost {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.RestTemplateServicePost' )

  @Pointcut(
    value='execution(Object mx.finerio.api.services.RestTemplateService.get(..)) && bean(restTemplateService) && args(url, headers, body)',
    argNames='url, headers, body'
  )
  public void post( String url, Map headers, Map body ) {}

  @Before('post(url, headers, body)')
  void before( String url, Map headers, Map body ) {
    log.info( "<< url: {}, headers: {}, body: {}", url, headers, body )
  }

  @AfterReturning(
    pointcut='post(String, java.util.Map, java.util.Map)',
    returning='response'
  )
  void afterReturning( Object response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='post(String, java.util.Map, java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
