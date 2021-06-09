package mx.finerio.api.aop

import mx.finerio.api.domain.Budget
import mx.finerio.api.dtos.pfm.BudgetDto
import mx.finerio.api.validation.BudgetUpdateCommand
import org.aspectj.lang.annotation.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@Aspect
class BudgetServiceUpdate {


  final static Logger log = LoggerFactory.getLogger(
          'mx.finerio.api.aop.BudgetServiceUpdate' )

  @Pointcut(
          value='execution(mx.finerio.api.domain.Budget mx.finerio.api.services.BudgetService.update(..))  && args(cmd, budget)',
          argNames='cmd, budget'
  )
  public void update(BudgetUpdateCommand cmd, Budget budget) {}

  @Before('update(cmd, budget)')
  void before( BudgetUpdateCommand cmd, Budget budget ) {
    log.info( "<< cmd: {}, budget: {}", cmd, budget )
  }

  @AfterReturning(
          pointcut='update(mx.finerio.api.validation.BudgetUpdateCommand, mx.finerio.api.domain.Budget)',
          returning='response'
  )
  void afterReturning(BudgetDto response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
          pointcut='update(mx.finerio.api.validation.BudgetUpdateCommand, mx.finerio.api.domain.Budget)',
          throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
