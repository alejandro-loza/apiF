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
class SatwsClientServiceGetLinksByParams {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsClientServiceGetLinksByParams' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsClientService.getLinksByParams(..)) && bean(satwsClientService) && args(rfc,params)',
    argNames='rfc,params'
  )
  public void getLinksByParams( String rfc, Map params ) {}

  @Before('getLinksByParams(rfc,params)')
  void before( String rfc, Map params ) {
    log.info( "<< rfc: {}, params: {}", rfc, params )
  }

  @AfterReturning(
    pointcut='getLinksByParams(java.lang.String,java.util.Map)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getLinksByParams(java.lang.String,java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
