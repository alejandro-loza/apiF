package mx.finerio.api.aop

import mx.finerio.api.domain.Customer

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
class RsaCryptServiceSign {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.RsaCryptServiceSign' )

  @Pointcut(
    value='execution(String mx.finerio.api.services.RsaCryptService.sign(..)) && bean(rsaCryptService) && args(text)',
    argNames='text'
  )
  public void sign( String text ) {}

  @Before('sign(text)')
  void before( String text ) {
    log.info( "<< text: {}", text )
  }

  @AfterReturning(
    pointcut='sign(String)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( ">> response: {}" , response)
  }

  @AfterThrowing(
    pointcut='sign(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
