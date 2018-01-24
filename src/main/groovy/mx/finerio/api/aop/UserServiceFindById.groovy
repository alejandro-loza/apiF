package mx.finerio.api.aop

import mx.finerio.api.domain.*

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
class UserServiceFindById {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.UserServiceFindById' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.User mx.finerio.api.services.UserService.findById(..)) && bean(userService) && args(id)',
    argNames='id'
  )
  public void findById( String id ) {}

  @Before('findById(id)')
  void before( String id ) {
    log.info( "<< id: {}", id )
  }

  @AfterReturning(
    pointcut='findById(String)',
    returning='response'
  )
  void afterReturning( User response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findById(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
