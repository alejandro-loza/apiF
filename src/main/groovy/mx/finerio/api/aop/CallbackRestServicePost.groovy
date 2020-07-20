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
class CallbackRestServicePost {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CallbackRestServicePost' )

  @Pointcut(
    value='execution(Integer mx.finerio.api.services.CallbackRestService.post(..)) && bean(callbackRestService) && args(url, body)',
    argNames='url, body'
  )
  public void post( String url, Object body ) {}

  @Before('post(url, body)')
  void before( String url, Object body ) {
    log.info( "<< url: {}, body: {}", url, body )
  }

  @AfterReturning(
    pointcut='post(String, Object)',
    returning='response'
  )
  void afterReturning( Integer response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='post(String, Object)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
