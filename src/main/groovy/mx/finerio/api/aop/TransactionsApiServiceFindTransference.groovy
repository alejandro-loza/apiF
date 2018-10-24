package mx.finerio.api.aop

import org.aspectj.lang.annotation.*
import org.slf4j.*
import org.springframework.stereotype.Component

@Component
@Aspect
class TransactionsApiServiceFindTransference {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.TransactionsApiServiceFindTransference' )

  @Pointcut(
    value='execution(java.util.List mx.finerio.api.services.TransactionsApiService.findTransference(..)) && bean(transactionsApiService) && args(map)',
    argNames='map'
  )
  public void findTransference( Map map ) {}

  @Before('findTransference(map)')
  void before( Map map ) {
    log.info( "<< map: {}", map )
  }

  @AfterReturning(
    pointcut='findTransference(java.util.Map)',
    returning='response'
  )
  void afterReturning( List response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findTransference(java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
