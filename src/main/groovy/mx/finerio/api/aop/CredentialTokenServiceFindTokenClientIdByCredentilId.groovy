package mx.finerio.api.aop

import mx.finerio.api.domain.Credential

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
class CredentialTokenServiceFindTokenClientIdByCredentilId {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialTokenServiceFindTokenClientIdByCredentilId' )

  @Pointcut(
    value='execution(java.lang.String mx.finerio.api.services.CredentialTokenService.findTokenClientIdByCredentilId(..)) && bean(credentialTokenService) && args(credentialId)',
    argNames='credentialId'
  )
  public void findTokenClientIdByCredentilId( String credentialId ) {}

  @Before('findTokenClientIdByCredentilId(credentialId)')
  void before( String credentialId ) {
    log.info( "<< credentialId: {}", credentialId )
  }

  @AfterReturning(
    pointcut='findTokenClientIdByCredentilId(String)',
    returning='tokenClientId'
  )
  void afterReturning( String tokenClientId ) {
    log.info( '>> tokenClientId: {}', tokenClientId )
  }

  @AfterThrowing(
    pointcut='findTokenClientIdByCredentilId(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
