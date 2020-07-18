package mx.finerio.api.aop

import mx.finerio.api.domain.CredentialToken

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
class CredentialTokenServiceSaveUpdateCredentialToken {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialTokenServiceSaveUpdateCredentialToken' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.CredentialToken mx.finerio.api.services.CredentialTokenService.saveUpdateCredentialToken(..)) && bean(credentialTokenService) && args(credentialId, tokenClientId)',
    argNames='credentialId, tokenClientId'
  )
  public void saveUpdateCredentialToken( String credentialId, String tokenClientId ) {}

  @Before('saveUpdateCredentialToken(credentialId, tokenClientId)')
  void before( String credentialId, String tokenClientId ) {
    log.info( "<< credentialId: {}, tokenClientId: {}", credentialId, tokenClientId )
  }

  @AfterReturning(
    pointcut='saveUpdateCredentialToken(String, String)',
    returning='credentialToken'
  )
  void afterReturning( CredentialToken credentialToken ) {
    log.info( '>> credentialtoken: {}', credentialToken )
  }

  @AfterThrowing(
    pointcut='saveUpdateCredentialToken(String, String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
