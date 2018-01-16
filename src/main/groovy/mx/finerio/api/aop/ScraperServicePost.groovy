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
class ScraperServicePost {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperServicePost' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.DevScraperService.post(..)) && bean(devScraperService) && args(path, data)',
    argNames='path, data'
  )
  public void post(String path, Map data ) {}

  @Before('post(path, data)')
  void before(String path, Map data ) {
    log.info( "<< path: {}, data: {}", path, data )
  }

  @AfterReturning(
    pointcut='post(String, java.util.Map)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='post(String, java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
