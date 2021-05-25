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
class SatwsServiceDeleteLink {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceDeleteLink' )

  @Pointcut(
    value='execution(java.lang.String mx.finerio.api.services.SatwsService.deleteLink(..)) && bean(satwsService) && args(linkId)',
    argNames='linkId'
  )
  public void deleteLink( String linkId  ) {}

  @Before('deleteLink(linkId)')
  void before( String linkId  ) {
    log.info( "<< linkId: {}", linkId  )
  }

  @AfterReturning(
    pointcut='deleteLink(java.lang.String)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='deleteLink(java.lang.String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
