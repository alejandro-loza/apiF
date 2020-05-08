package mx.finerio.api.aop

import mx.finerio.api.domain.BankField

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
class BankFieldServiceGetFields {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.BankFieldServiceGetFields' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.BankFieldService.getFields(..)) && bean(bankFieldService) && args(bankField)',
    argNames='bankField'
  )
  public void getFields( BankField bankField ) {}

  @Before('getFields(bankField)')
  void before( BankField bankField ) {
    log.info( "<< bankField: {}", bankField )
  }

  @AfterReturning(
    pointcut='getFields(mx.finerio.api.domain.BankField)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getFields(mx.finerio.api.domain.BankField)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
