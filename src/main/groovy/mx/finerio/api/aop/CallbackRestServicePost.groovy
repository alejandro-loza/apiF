package mx.finerio.api.aop

import mx.finerio.api.dtos.MtlsDto

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
class CallbackRestServicePost {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CallbackRestServicePost' )

  @Pointcut(
    value='execution(Integer mx.finerio.api.services.CallbackRestService.post(..)) && bean(callbackRestService) && args(url, body, headers)',
    argNames='url, body, headers'
  )
  public void post( String url, Object body, Map headers ) {}

  @Before('post(url, body, headers)')
  void before( String url, Object body, Map headers ) {
    log.info( "<< url: {}, body: {}", url, body )
  }

  @AfterReturning(
    pointcut='post(String, Object, java.util.Map)',
    returning='response'
  )
  void afterReturning( Integer response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='post(String, Object, java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

  @Pointcut(
    value='execution(Integer mx.finerio.api.services.CallbackRestService.post(..)) && bean(callbackRestService) && args(url, body, headers, mtlsDto)',
    argNames='url, body, headers, mtlsDto'
  )
  public void post2( String url, Object body, Map headers, MtlsDto mtlsDto ) {}

  @Before('post2(url, body, headers, mtlsDto)')
  void before2( String url, Object body, Map headers, MtlsDto mtlsDto ) {
    log.info( "<< url: {}, body: {}", url, body )
  }

  @AfterReturning(
    pointcut='post2(String, Object, java.util.Map, mx.finerio.api.dtos.MtlsDto)',
    returning='response'
  )
  void afterReturning2( Integer response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='post2(String, Object, java.util.Map, mx.finerio.api.dtos.MtlsDto)',
    throwing='e'
  )
  void afterThrowing2( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
