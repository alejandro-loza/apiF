package mx.finerio.api.aop

import mx.finerio.api.domain.Credential
import mx.finerio.api.dtos.ApiListDto

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
class CredentialErrorServiceFindAll {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialErrorServiceFindAll' )

  @Pointcut(
    value='execution(mx.finerio.api.dtos.ApiListDto mx.finerio.api.services.CredentialErrorService.findAll()) && bean(credentialErrorService)'
  )
  public void findAll() {}

  @Before('findAll()')
  void before() {
    log.info( "<< OK" )
  }

  @AfterReturning(
    pointcut='findAll()',
    returning='response'
  )
  void afterReturning( ApiListDto response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findAll()',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
