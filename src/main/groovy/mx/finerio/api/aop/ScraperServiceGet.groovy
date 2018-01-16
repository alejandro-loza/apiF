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
class ScraperServiceLogin {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperServiceLogin' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.DevScraperService.login(..)) && bean(devScraperService) && args()',
    argNames=''
  )
  public void login() {}
/*
  @Before('login()')
  void before(  ) {
    log.info( "<< path: {}, data: {}", path, data )
  }
*/
  @AfterReturning(
    pointcut='login()',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='login()',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
