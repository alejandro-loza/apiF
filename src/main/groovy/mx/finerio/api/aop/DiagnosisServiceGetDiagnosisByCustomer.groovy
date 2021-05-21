package mx.finerio.api.aop

import mx.finerio.api.dtos.DiagnosisDto
import org.aspectj.lang.annotation.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@Aspect
class DiagnosisServiceGetDiagnosisByCustomer {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.DiagnosisServiceGetDiagnosisByCustomer' )

  @Pointcut(
    value='execution(mx.finerio.api.dtos.DiagnosisDto mx.finerio.api.services.DiagnosisService.getDiagnosisByCustomer(..))  && args(customerId, averageManualIncome)',
    argNames='customerId, averageManualIncome'
  )
  void getDiagnosisByCustomer( Long customerId , Optional averageManualIncome) {}

  @Before('getDiagnosisByCustomer(customerId, averageManualIncome)')
  void before( Long customerId, Optional averageManualIncome ) {
    log.info( "<< customerId: {}, averageManualIncome: {}", customerId, averageManualIncome )
  }

  @AfterReturning(
    pointcut='getDiagnosisByCustomer(java.lang.Long, java.util.Optional)',
    returning='response'
  )
  void afterReturning( DiagnosisDto response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getDiagnosisByCustomer(java.lang.Long, java.util.Optional)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
