package mx.finerio.api.aop

import mx.finerio.api.dtos.pfm.BudgetDto
import org.aspectj.lang.annotation.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@Aspect
class BudgetServiceFindById {

  final static Logger log = LoggerFactory.getLogger(
          'mx.finerio.api.aop.BudgetServiceFindById' )

  @Pointcut(
          value='execution(mx.finerio.api.dtos.pfm.BudgetDto mx.finerio.api.services.imp.BudgetServiceImp.findById(..)) && args(id)',
          argNames='id'
  )

  public void findById(Long id) {}

  @Before('findById(id)')
  void before( Long id ) {
    log.info( "<< id: {}", id )
  }

  @AfterReturning(
          pointcut='findById(java.lang.Long)',
          returning='response'
  )

  void afterReturning(BudgetDto response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
          pointcut='findById(java.lang.Long)',
          throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }


}
