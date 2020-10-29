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
class CustomerServiceFindOne2 {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CustomerServiceFindOne2' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Customer mx.finerio.api.services.CustomerService.findOne(..)) && bean(customerService) && args(id,client)',
    argNames='id,client'
  )
  public void findOne( Long id, Client client ) {}

  @Before('findOne(id,client)')
  void before( Long id, Client client ) {
    log.info( "<< id: {}, client: {}", id, client )
  }

  @AfterReturning(
    pointcut='findOne(Long,mx.finerio.api.domain.Client)',
    returning='response'
  )
  void afterReturning( Customer response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOne(Long,mx.finerio.api.domain.Client)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
