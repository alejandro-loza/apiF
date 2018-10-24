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
    value='execution(String mx.finerio.api.services.CleanerService.clean(..)) && bean(cleanerService) && args(text, income)',
    argNames='text, income'
  )
  public void clean( String text, Boolean income ) {}

  @Before('clean(text, income)')
  void before( String text, Boolean income ) {
    log.info( "<< text: {}, income: {}", text, income )
  }

  @AfterReturning(
    pointcut='clean(String, Boolean)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='clean(String, Boolean)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
