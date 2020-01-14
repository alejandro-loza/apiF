package mx.finerio.api.aop 

import mx.finerio.api.domain.*
import mx.finerio.api.dtos.*
import org.aspectj.lang.annotation.*
import org.slf4j.*
import org.springframework.stereotype.Component

@Component
@Aspect
class ClientServiceCreate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ClientServiceCreate' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.ClientService.create(..)) && bean(clientService) && args(dto)',
    argNames='dto'
  )
  public void create( ClientDto dto ) {}

  @Before('create(dto)')
  void before( ClientDto dto ) {
    log.info( "<< dto: {}", dto )
  }

  @AfterReturning(
    pointcut='create(mx.finerio.api.dtos.ClientDto)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(mx.finerio.api.dtos.ClientDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
