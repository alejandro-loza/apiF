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
class SatwsServiceGetLink {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceGetLink' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsService.getLink(..)) && bean(satwsService) && args(linkId)',
    argNames='linkId'
  )
  public void getLink( String linkId  ) {}

  @Before('getLink(linkId)')
  void before( String linkId  ) {
    log.info( "<< linkId: {}", linkId  )
  }

  @AfterReturning(
    pointcut='getLink(java.lang.String)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getLink(java.lang.String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
