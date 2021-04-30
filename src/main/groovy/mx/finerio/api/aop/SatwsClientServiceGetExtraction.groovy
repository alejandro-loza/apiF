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
class SatwsClientServiceGetExtraction {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsClientServiceGetExtraction' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.SatwsClientService.getExtraction(..)) && bean(satwsClientService) && args(extractionId)',
    argNames='extractionId'
  )
  public void getExtraction( String extractionId  ) {}

  @Before('getExtraction(extractionId)')
  void before( String extractionId  ) {
    log.info( "<< extractionId: {}", extractionId  )
  }

  @AfterReturning(
    pointcut='getExtraction(java.lang.String)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getExtraction(java.lang.String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
