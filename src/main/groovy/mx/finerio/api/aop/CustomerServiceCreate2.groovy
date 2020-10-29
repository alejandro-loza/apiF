package mx.finerio.api.aop

import mx.finerio.api.dtos.CustomerDto
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
class CustomerServiceCreate2 {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CustomerServiceCreate2' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Customer mx.finerio.api.services.CustomerService.create(..)) && bean(customerService) && args(dto,client)',
    argNames='dto,client'
  )
  public void create( CustomerDto dto, Client client ) {}

  @Before('create(dto,client)')
  void before( CustomerDto dto, Client client ) {
    log.info( "<< customerDto: {}, client: {}", dto, client )
  }

  @AfterReturning(
    pointcut='create(mx.finerio.api.dtos.CustomerDto,mx.finerio.api.domain.Client)',
    returning='response'
  )
  void afterReturning( Customer response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(mx.finerio.api.dtos.CustomerDto,mx.finerio.api.domain.Client)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
