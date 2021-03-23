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
class AccountServiceFindByIdAndCredentialId {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountServiceFindByIdAndCredentialId' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Account mx.finerio.api.services.AccountService.findByIdAndCredentialId(..)) && bean(accountService) && args(id,credentialId)',
    argNames='id,credentialId'
  )
  public void findByIdAndCredentialId( String id, String credentialId ) {}

  @Before('findByIdAndCredentialId(id,credentialId)')
  void before( String id, String credentialId ) {
    log.info( "<< id: {}, credentialId: {}", id, credentialId )
  }

  @AfterReturning(
    pointcut='findByIdAndCredentialId(String,String)',
    returning='response'
  )
  void afterReturning( Account response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findByIdAndCredentialId(String,String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
