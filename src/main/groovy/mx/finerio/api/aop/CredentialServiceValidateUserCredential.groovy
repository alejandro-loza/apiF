package mx.finerio.api.aop

import mx.finerio.api.domain.Credential

import org.aspectj.lang.annotation.*
import org.slf4j.*
import org.springframework.stereotype.Component

@Component
@Aspect
class CredentialServiceValidateUserCredential {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceValidateUserCredential' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Credential mx.finerio.api.services.CredentialService.validateUserCredential(..)) && bean(credentialService) && args(credential,userId)',
    argNames='credential,userId'
  )
  public void validateUserCredential( Credential credential, String userId ) {}

  @Before('validateUserCredential(credential,userId)')
  void before( Credential credential, String userId ) {
    log.info( "<< credential: {}, userId: {}", credential, userId )
  }

  @AfterReturning(
    pointcut='validateUserCredential(mx.finerio.api.domain.Credential,String)',
    returning='response'
  )
  void afterReturning( Credential response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='validateUserCredential(mx.finerio.api.domain.Credential,String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
