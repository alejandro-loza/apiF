package mx.finerio.api.aop

import mx.finerio.api.domain.*

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
@Aspect
class MovementServiceFindByAccount {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.MovementServiceFindByAccount' )

  @Pointcut(
    value='execution(Object mx.finerio.api.services.MovementService.findByAccount(..)) && bean(movementService) && args(id, pageable)',
    argNames='id, pageable'
  )
  public void findByAccount( String id, Pageable pageable ) {}

  @Before('findByAccount(id, pageable)')
  void before( String id, Pageable pageable ) {
    log.info( "<< id: {}, pageable: {}", id, pageable )
  }

  @AfterReturning(
    pointcut='findByAccount(String, org.springframework.data.domain.Pageable)',
    returning='response'
  )
  void afterReturning( Object response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findByAccount(String, org.springframework.data.domain.Pageable)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
