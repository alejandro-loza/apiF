package mx.finerio.api.aop

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
class SatwsServiceDeleteTaxReturn {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceDeleteTaxReturn' )

  @Pointcut(
    value='execution(java.lang.String mx.finerio.api.services.SatwsService.deleteTaxReturn(..)) && bean(satwsService) && args(taxReturnId)',
    argNames='taxReturnId'
  )
  public void deleteTaxReturn( String taxReturnId  ) {}

  @Before('deleteTaxReturn(taxReturnId)')
  void before( String taxReturnId  ) {
    log.info( "<< taxReturnId: {}", taxReturnId  )
  }

  @AfterReturning(
    pointcut='deleteTaxReturn(java.lang.String)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='deleteTaxReturn(java.lang.String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
