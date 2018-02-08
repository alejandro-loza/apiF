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
class CustomerServiceGetFields {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CustomerServiceGetFields' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.CustomerService.getFields(..)) && bean(customerService) && args(customer)',
    argNames='customer'
  )
  public void getFields( Customer customer ) {}

  @Before('getFields(customer)')
  void before( Customer customer ) {
    log.info( "<< customer: {}", customer )
  }

  @AfterReturning(
    pointcut='getFields(mx.finerio.api.domain.Customer)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getFields(mx.finerio.api.domain.Customer)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
