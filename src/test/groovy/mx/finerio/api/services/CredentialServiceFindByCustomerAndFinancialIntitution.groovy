package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.dtos.ListDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException

import org.springframework.data.jpa.repository.JpaRepository

import spock.lang.Specification

class CredentialServiceFindByCustomerAndFinancialIntitutionSpec extends Specification {

  def service = new CredentialService()
  
  def credentialRepository = Mock( CredentialRepository )

  def setup() {
    service.credentialRepository = credentialRepository
  }

  def "invoking method successfully"() {

    when:
      def result = service.findByCustomerAndFinancialIntitution( customer, financialInstitution )
    then:
      1 * credentialRepository.findByCustomerAndInstitutionAndDateDeletedIsNull( _ as Customer, _ as FinancialInstitution )>> new Credential()    
      result instanceof Credential
    where:
      customer = new Customer()
      financialInstitution = new FinancialInstitution()

  }

  def "customer param is null"() {

    when:
      service.findByCustomerAndFinancialIntitution( customer, financialInstitution )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findByCustomerAndFinancialIntitution.customer.null'
    where:
      customer = null
      financialInstitution = new FinancialInstitution()

  }


  def "financialInstitution param is null"() {

    when:
      service.findByCustomerAndFinancialIntitution( customer, financialInstitution )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findByCustomerAndFinancialIntitution.financialInstitution.null'
    where:
      customer =  new Customer()
      financialInstitution = null
  }

}
