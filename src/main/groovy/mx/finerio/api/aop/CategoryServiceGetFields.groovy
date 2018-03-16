package mx.finerio.api.aop

import mx.finerio.api.domain.Category

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
class CategoryServiceGetFields {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CategoryServiceGetFields' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.CategoryService.getFields(..)) && bean(categoryService) && args(category)',
    argNames='category'
  )
  public void getFields( Category category ) {}

  @Before('getFields(category)')
  void before( Category category ) {
    log.info( "<< category: {}", category )
  }

  @AfterReturning(
    pointcut='getFields(mx.finerio.api.domain.Category)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getFields(mx.finerio.api.domain.Category)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
