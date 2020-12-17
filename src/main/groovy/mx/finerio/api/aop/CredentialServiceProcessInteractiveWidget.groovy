package mx.finerio.api.aop

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import mx.finerio.api.dtos.CredentialInteractiveWidgetDto

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component

@Component
@Aspect
class CredentialServiceProcessInteractiveWidget {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceProcessInteractiveWidget' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.CredentialService.processInteractiveWidget(..)) && bean(credentialService) && args(credentialInteractiveWidgetDto)',
    argNames='credentialInteractiveWidgetDto'
  )
  public void processInteractiveWidget( CredentialInteractiveWidgetDto credentialInteractiveWidgetDto ) {}

  @Before('processInteractiveWidget(credentialInteractiveWidgetDto)')
  void before( CredentialInteractiveWidgetDto credentialInteractiveWidgetDto ) {
    log.info( "<< credentialInteractiveWidgetDto: {}", credentialInteractiveWidgetDto )
  }

  @AfterReturning(
    pointcut='processInteractiveWidget(mx.finerio.api.dtos.CredentialInteractiveWidgetDto)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='processInteractiveWidget(mx.finerio.api.dtos.CredentialInteractiveWidgetDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
