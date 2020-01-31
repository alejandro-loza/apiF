package mx.finerio.api.aop 

import mx.finerio.api.domain.*
import mx.finerio.api.dtos.*
import org.aspectj.lang.annotation.*
import org.slf4j.*
import org.springframework.stereotype.Component

@Component
@Aspect
class ClientServiceUpdate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ClientServiceUpdate' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.ClientService.update(..)) && bean(clientService) && args(dto)',
    argNames='dto'
  )
  public void update( UpdateClientDto dto ) {}

  @Before('update(dto)')
  void before( UpdateClientDto dto ) {
    log.info( "<< dto: {}",  dto )
  }

  @AfterReturning(
    pointcut='update(mx.finerio.api.dtos.UpdateClientDto)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='update(mx.finerio.api.dtos.UpdateClientDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
