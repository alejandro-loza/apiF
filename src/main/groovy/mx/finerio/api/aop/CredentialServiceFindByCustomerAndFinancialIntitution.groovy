package mx.finerio.api.aop

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
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
class CredentialServiceFindByCustomerAndFinancialIntitution {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CredentialServiceFindByCustomerAndFinancialIntitution' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Credential mx.finerio.api.services.CredentialService.findByCustomerAndFinancialIntitution(..)) && bean(credentialService) && args(customer,financialInstitution)',
    argNames='customer,financialInstitution'
  )
  public void findByCustomerAndFinancialIntitution( Customer customer, FinancialInstitution financialInstitution ) {}

  @Before('findByCustomerAndFinancialIntitution(customer,financialInstitution)')
  void before( Customer customer, FinancialInstitution financialInstitution ) {
    log.info( "<< customer: {}, financialInstitution: {}", customer, financialInstitution)
  }

  @AfterReturning(
    pointcut='findByCustomerAndFinancialIntitution(mx.finerio.api.domain.Customer,mx.finerio.api.domain.FinancialInstitution)',
    returning='response'
  )
  void afterReturning( Credential response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findByCustomerAndFinancialIntitution(mx.finerio.api.domain.Customer,mx.finerio.api.domain.FinancialInstitution)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
