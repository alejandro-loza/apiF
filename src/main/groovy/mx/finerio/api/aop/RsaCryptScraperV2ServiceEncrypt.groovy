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
class RsaCryptScraperV2ServiceEncrypt {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.RsaCryptScraperV2ServiceEncrypt' )

  @Pointcut(
    value='execution(String mx.finerio.api.services.RsaCryptScraperV2Service.encrypt(..)) && bean(rsaCryptScraperV2Service) && args(text)',
    argNames='text'
  )
  public void encrypt( String text ) {}

  @Before('encrypt(text)')
  void before( String text ) {
    log.info( "<< text: {}", text )
  }

  @AfterReturning(
    pointcut='encrypt(String)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( ">> response: {}" , response)
  }

  @AfterThrowing(
    pointcut='encrypt(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
