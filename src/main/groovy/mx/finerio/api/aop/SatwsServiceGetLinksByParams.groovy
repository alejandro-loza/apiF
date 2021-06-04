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
class SatwsServiceGetLinksByParams {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceGetLinksByParams' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsService.getLinksByParams(..)) && bean(satwsService) && args(params)',
    argNames='params'
  )
  public void getLinksByParams( Map params ) {}

  @Before('getLinksByParams(params)')
  void before( Map params ) {
    log.info( "<<params: {}", params )
  }

  @AfterReturning(
    pointcut='getLinksByParams(java.util.Map)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getLinksByParams(java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
