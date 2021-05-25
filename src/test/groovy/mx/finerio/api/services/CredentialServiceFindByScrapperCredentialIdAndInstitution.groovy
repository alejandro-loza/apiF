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

class CredentialServiceFindByfindByScrapperCredentialIdAndInstitutionSpec extends Specification {

  def service = new CredentialService()
  
  def credentialRepository = Mock( CredentialRepository )

  def setup() {
    service.credentialRepository = credentialRepository
  }

  def "invoking method successfully"() {

    when:
      def result = service.findByScrapperCredentialIdAndInstitution( scrapperCredentialId, financialInstitution )
    then:
      1 * credentialRepository.findByScrapperCredentialIdAndInstitutionAndDateDeletedIsNull( _ as String, _ as FinancialInstitution )>> new Credential()    
      result instanceof Credential
    where:
      scrapperCredentialId = 'rgrg-hew-serer-sfdsf'
      financialInstitution = new FinancialInstitution()

  }

  def "scrapperCredentialId param is null"() {

    when:
      service.findByScrapperCredentialIdAndInstitution( scrapperCredentialId, financialInstitution )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findByCustomerAndFinancialIntitution.scrapperCredentialId.null'
    where:
      scrapperCredentialId = null
      financialInstitution = new FinancialInstitution()

  }


  def "financialInstitution param is null"() {

    when:
      service.findByScrapperCredentialIdAndInstitution( scrapperCredentialId, financialInstitution )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findByCustomerAndFinancialIntitution.financialInstitution.null'
    where:
      scrapperCredentialId = 'rgrg-hew-serer-sfdsf'
      financialInstitution = null

  }

}
