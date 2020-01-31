package mx.finerio.api.aop 

import mx.finerio.api.domain.*
import org.aspectj.lang.annotation.*
import org.slf4j.*
import org.springframework.stereotype.Component

@Component
@Aspect
class ClientServiceFindOne {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ClientServiceFindOne' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.ClientService.findOne(..)) && bean(clientService) && args()',
    argNames=''
  )
  public void findOne() {}

  @Before('findOne()')
  void before(   ) {
    log.info( "<< Start process.",  )
  }

  @AfterReturning(
    pointcut='findOne()',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOne()',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
