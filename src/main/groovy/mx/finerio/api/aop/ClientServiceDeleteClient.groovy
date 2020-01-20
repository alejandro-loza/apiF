package mx.finerio.api.aop 

import mx.finerio.api.domain.*
import org.aspectj.lang.annotation.*
import org.slf4j.*
import org.springframework.stereotype.Component

@Component
@Aspect
class ClientServiceDeleteClient {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ClientServiceDeleteClient' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.ClientService.deleteClient(..)) && bean(clientService) && args()',
    argNames=''
  )
  public void deleteClient(   ) {}

  @Before('deleteClient()')
  void before(   ) {
    log.info( "<< Start process.",  )
  }

  @AfterReturning(
    pointcut='deleteClient()',
    returning='response'
  )
  void afterReturning() {
    log.info( '>> response: OK' )
  }

  @AfterThrowing(
    pointcut='deleteClient()',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
