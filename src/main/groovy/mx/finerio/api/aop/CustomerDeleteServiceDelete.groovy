package mx.finerio.api.aop

import mx.finerio.api.domain.Customer

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
class CustomerDeleteServiceDelete {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CustomerDeleteServiceDelete' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.CustomerDeleteService.delete(..)) && bean(customerDeleteService) && args(id)',
    argNames='id'
  )
  public void deleteMethod( Long id ) {}

  @Before('deleteMethod(id)')
  void before( Long id ) {
    log.info( "<< customerId: {}", id )
  }

  @AfterReturning(
    pointcut='deleteMethod(Long)'
  )
  void afterReturning() {
    log.info( '>> response: OK' )
  }

  @AfterThrowing(
    pointcut='deleteMethod(Long)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
