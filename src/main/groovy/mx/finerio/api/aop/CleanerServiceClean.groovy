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
class CleanerServiceClean {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CleanerServiceClean' )

  @Pointcut(
    value='execution(String mx.finerio.api.services.CleanerService.clean(..)) && bean(cleanerService) && args(text)',
    argNames='text'
  )
  public void clean( String text ) {}

  @Before('clean(text)')
  void before( String text ) {
    log.info( "<< text: {}", text )
  }

  @AfterReturning(
    pointcut='clean(String)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='clean(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
