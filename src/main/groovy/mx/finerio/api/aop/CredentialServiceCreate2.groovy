package mx.finerio.api.aop

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.Client
import mx.finerio.api.dtos.CredentialDto

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
class CredentialServiceCreate2 {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceCreate2' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Credential mx.finerio.api.services.CredentialService.create(..)) && bean(credentialService) && args(credentialDto,customer,client)',
    argNames='credentialDto,customer,client'
  )
  public void create( CredentialDto credentialDto, Customer customer, Client client  ) {}

  @Before('create(credentialDto,customer,client)')
  void before( CredentialDto credentialDto, Customer customer, Client client ) {
    log.info( "<< credentialDto: {}, customer: {}, client: {}", credentialDto, customer, client )
  }

  @AfterReturning(
    pointcut='create(mx.finerio.api.dtos.CredentialDto,mx.finerio.api.domain.Customer,mx.finerio.api.domain.Client)',
    returning='response'
  )
  void afterReturning( mx.finerio.api.domain.Credential response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(mx.finerio.api.dtos.CredentialDto,mx.finerio.api.domain.Customer,mx.finerio.api.domain.Client)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
