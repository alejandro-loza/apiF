package mx.finerio.api.aop

import mx.finerio.api.dtos.CustomerListDto

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
class CustomerServiceFindAll {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CustomerServiceFindAll' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.CustomerService.findAll(..)) && bean(customerService) && args(dto)',
    argNames='dto'
  )
  public void findAll( CustomerListDto dto ) {}

  @Before('findAll(dto)')
  void before( CustomerListDto dto ) {
    log.info( "<< customerListDto: {}", dto )
  }

  @AfterReturning(
    pointcut='findAll(mx.finerio.api.dtos.CustomerListDto)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findAll(mx.finerio.api.dtos.CustomerListDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
