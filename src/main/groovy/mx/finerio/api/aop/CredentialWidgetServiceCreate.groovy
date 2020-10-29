package mx.finerio.api.aop

import mx.finerio.api.domain.Credential
import mx.finerio.api.dtos.CredentialWidgetDto

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
class CredentialWidgetServiceCreate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialWidgetServiceCreate' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.CredentialWidgetService.create(..)) && bean(credentialWidgetService) && args(credentialWidgetDto)',
    argNames='credentialWidgetDto'
  )
  public void create( CredentialWidgetDto credentialWidgetDto ) {}

  @Before('create(credentialWidgetDto)')
  void before( CredentialWidgetDto credentialWidgetDto ) {
    log.info( "<< credentialWidgetDto: {}", credentialWidgetDto )
  }

  @AfterReturning(
    pointcut='create(mx.finerio.api.dtos.CredentialWidgetDto)',
    returning='response'
  )
  void afterReturning( java.util.Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(mx.finerio.api.dtos.CredentialWidgetDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}