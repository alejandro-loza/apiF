package mx.finerio.api.aop

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientMtls

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
class ClientMtlsServiceFindByClient {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ClientMtlsServiceFindByClient' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.ClientMtls mx.finerio.api.services.ClientMtlsService.findByClient(..)) && bean(clientMtlsService) && args(client)',
    argNames='client'
  )
  public void findByClient( Client client ) {}

  @Before('findByClient(client)')
  void before( Client client ) {
    log.info( "<< client: {}", client )
  }

  @AfterReturning(
    pointcut='findByClient(mx.finerio.api.domain.Client)',
    returning='clientMtls'
  )
  void afterReturning( ClientMtls clientMtls ) {
    log.info( '>> clientMtls: {}', clientMtls )
  }

  @AfterThrowing(
    pointcut='findByClient(mx.finerio.api.domain.Client)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
