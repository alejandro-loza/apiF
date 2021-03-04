package mx.finerio.api.aop

import mx.finerio.api.dtos.AnalysisDto

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
class AnalysisServiceGetAnalysisByCustomer {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AnalysisServiceGetAnalysisByCustomer' )

  @Pointcut(
    value='execution(mx.finerio.api.dtos.AnalysisDto mx.finerio.api.services.AnalysisService.getAnalysisByCustomer(..)) && bean(analysisService) && args(customerId)',
    argNames='customerId'
  )
  public void getAnalysisByCustomer( Long customerId ) {}

  @Before('getAnalysisByCustomer(customerId)')
  void before( Long customerId ) {
    log.info( "<< customerId: {}", customerId )
  }

  @AfterReturning(
    pointcut='getAnalysisByCustomer(Long)',
    returning='response'
  )
  void afterReturning( AnalysisDto response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getAnalysisByCustomer(Long)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
