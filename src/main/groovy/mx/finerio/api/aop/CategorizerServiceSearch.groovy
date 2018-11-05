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
class CategorizerServiceSearch {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CategorizerServiceSearch' )

  @Pointcut(
    value='execution(Object mx.finerio.api.services.CategorizerService.search(..)) && bean(categorizerService) && args(text, income)',
    argNames='text, income'
  )
  public void search( String text, Boolean income ) {}

  @Before('search(text, income)')
  void before( String text, Boolean income ) {
    log.info( "<< text: {}, income: {}", text, income )
  }

  @AfterReturning(
    pointcut='search(String, Boolean)',
    returning='response'
  )
  void afterReturning( Object response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='search(String, Boolean)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
