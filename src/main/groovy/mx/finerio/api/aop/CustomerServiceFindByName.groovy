package mx.finerio.api.aop

import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.Client

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
class CustomerServiceFindByName {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CustomerServiceFindByName' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Customer mx.finerio.api.services.CustomerService.findByName(..)) && bean(customerService) && args(client,name)',
    argNames='client,name'
  )
  public void findOne( Client client, String name ) {}

  @Before('findOne(client,name)')
  void before( Client client, String name ) {
    log.info( "<< client: {}, name: {}", client, name )
  }

  @AfterReturning(
    pointcut='findOne(mx.finerio.api.domain.Client, String)',
    returning='response'
  )
  void afterReturning( Customer response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOne(mx.finerio.api.domain.Client, String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
