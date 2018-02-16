package mx.finerio.api.services

import mx.finerio.api.domain.CredentialFailureMessage
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.CredentialFailureMessageRepository
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class CredentialFailureMessageFindByInstitutionAndMessageSpec extends Specification {

  def service = new CredentialFailureMessageService()

  def credentialFailureMessageRepository = Mock(
      CredentialFailureMessageRepository )

  def setup() {
    service.credentialFailureMessageRepository =
        credentialFailureMessageRepository
  }

  def "invoking method successfully" () {

    when:
      def result = service.findByInstitutionAndMessage( institution, message )
    then:
      1 * credentialFailureMessageRepository
          .findFirstByInstitutionAndOriginalMessage(
          _ as FinancialInstitution, _ as String ) >>
          new CredentialFailureMessage()
      result instanceof CredentialFailureMessage
    where:
      institution = new FinancialInstitution()
      message = 'message'

  }

  def "instance not found" () {

    when:
      def result = service.findByInstitutionAndMessage( institution, message )
    then:
      1 * credentialFailureMessageRepository
          .findFirstByInstitutionAndOriginalMessage(
          _ as FinancialInstitution, _ as String )
      result == null
    where:
      institution = new FinancialInstitution()
      message = 'message'

  }

  def "parameter 'institution' is null" () {

    when:
      service.findByInstitutionAndMessage( institution, message )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialFailureMessageService' +
          '.findByInstitutionAndMessage.institution.null'
    where:
      institution = null
      message = 'message'

  }

  def "parameter 'message' is null" () {

    when:
      service.findByInstitutionAndMessage( institution, message )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialFailureMessageService' +
          '.findByInstitutionAndMessage.message.null'
    where:
      institution = new FinancialInstitution()
      message = null

  }

  def "parameter 'message' is blank" () {

    when:
      service.findByInstitutionAndMessage( institution, message )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialFailureMessageService' +
          '.findByInstitutionAndMessage.message.null'
    where:
      institution = new FinancialInstitution()
      message = ''

  }

}
