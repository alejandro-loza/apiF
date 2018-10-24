package mx.finerio.api.aop;

import java.util.Map

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
class EmailRestServiceSend {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.EmailRestServiceSend' )

  @Pointcut(
    value='execution(String mx.finerio.api.services.EmailRestService.send(..)) && bean(emailRestService) && args(email, template, params)',
    argNames='email, template, params'
  )
  public void send( String email, String template, Map params ) {}

  @Before('send(email, template, params)')
  void before( String email, String template, Map params ) {
    log.info( "<< email: {}, template: {}, params: {}", email, template, params )
  }

  @AfterReturning(
    pointcut='send(String, String, java.util.Map)',
    returning='result'
  )
  void afterReturning( String result ) {
    log.info( ">> result: {}", result )
  }

  @AfterThrowing(
    pointcut='send(String, String, java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}

