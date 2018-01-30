package mx.finerio.api.aop

import mx.finerio.api.dtos.CustomerDto
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
class CustomerServiceCreate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CustomerServiceCreate' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Customer mx.finerio.api.services.CustomerService.create(..)) && bean(customerService) && args(dto)',
    argNames='dto'
  )
  public void create( CustomerDto dto ) {}

  @Before('create(dto)')
  void before( CustomerDto dto ) {
    log.info( "<< customerDto: {}", dto )
  }

  @AfterReturning(
    pointcut='create(mx.finerio.api.dtos.CustomerDto)',
    returning='response'
  )
  void afterReturning( Customer response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(mx.finerio.api.dtos.CustomerDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
