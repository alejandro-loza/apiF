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
    value='execution(java.util.Map mx.finerio.api.services.CategorizerService.search(..)) && bean(categorizerService) && args(text)',
    argNames='text'
  )
  public void search( String text ) {}

  @Before('search(text)')
  void before( String text ) {
    log.info( "<< text: {}", text )
  }

  @AfterReturning(
    pointcut='search(String)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='search(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
