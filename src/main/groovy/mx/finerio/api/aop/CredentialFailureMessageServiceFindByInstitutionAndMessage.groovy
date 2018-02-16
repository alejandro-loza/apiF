package mx.finerio.api.aop

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
class CredentialFailureMessageServiceFindByInstitutionAndMessage {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialFailureMessageServiceFindByInstitutionAndMessage' )

  @Pointcut(
    value='execution(String mx.finerio.api.services.CredentialFailureMessageService.findByInstitutionAndMessage(..)) && bean(credentialFailureMessageService) && args(institution, message)',
    argNames='institution, message'
  )
  public void findByInstitutionAndMessage( FinancialInstitution institution,
      String message ) {}

  @Before('findByInstitutionAndMessage(institution, message)')
  void before( FinancialInstitution institution, String message ) {
    log.info( "<< institution: {}, message: {}", institution, message )
  }

  @AfterReturning(
    pointcut='findByInstitutionAndMessage(mx.finerio.api.domain.FinancialInstitution, String)',
    returning='message'
  )
  void afterReturning( String message ) {
    log.info( '>> message: {}', message )
  }

  @AfterThrowing(
    pointcut='findByInstitutionAndMessage(mx.finerio.api.domain.FinancialInstitution, String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
