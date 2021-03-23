package mx.finerio.api.aop

import mx.finerio.api.domain.*

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
class AccountServiceFindByIdBankAndCredentialId {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountServiceFindByIdBankAndCredentialId' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Account mx.finerio.api.services.AccountService.findByIdBankAndCredentialIdAndCredentialId(..)) && bean(accountService) && args(idBank,credentialId)',
    argNames='idBank,credentialId'
  )
  public void findByIdBankAndCredentialId( String idBank, String credentialId ) {}

  @Before('findByIdBankAndCredentialId(idBank,credentialId)')
  void before( String idBank, String credentialId ) {
    log.info( "<< idBank: {}, credentialId: {}", idBank, credentialId )
  }

  @AfterReturning(
    pointcut='findByIdBankAndCredentialId(String,String)',
    returning='response'
  )
  void afterReturning( Account response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findByIdBankAndCredentialId(String,String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
