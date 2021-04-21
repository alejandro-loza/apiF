package mx.finerio.api.aop

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.FinancialInstitution
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
class CredentialServiceFindByScrapperCredentialIdAndInstitution {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.findByScrapperCredentialIdAndInstitution' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Credential mx.finerio.api.services.CredentialService.findByScrapperCredentialIdAndInstitution(..)) && bean(credentialService) && args(scrapperCredentialId,financialInstitution)',
    argNames='scrapperCredentialId,financialInstitution'
  )
  public void findByScrapperCredentialIdAndInstitution( String scrapperCredentialId, FinancialInstitution financialInstitution ) {}

  @Before('findByScrapperCredentialIdAndInstitution(scrapperCredentialId,financialInstitution)')
  void before( String scrapperCredentialId, FinancialInstitution financialInstitution ) {
    log.info( "<< scrapperCredentialId: {}, financialInstitution: {}", scrapperCredentialId, financialInstitution)
  }

  @AfterReturning(
    pointcut='findByScrapperCredentialIdAndInstitution(java.lang.String,mx.finerio.api.domain.FinancialInstitution)',
    returning='response'
  )
  void afterReturning( Credential response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findByScrapperCredentialIdAndInstitution(java.lang.String,mx.finerio.api.domain.FinancialInstitution)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
