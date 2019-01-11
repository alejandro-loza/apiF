package mx.finerio.api.services

import mx.finerio.api.domain.CredentialFailureMessage
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.CredentialFailureMessageRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.domain.Credential

import spock.lang.Specification

class CredentialFailureMessageFindByInstitutionAndMessageSpec extends Specification {

  def service = new CredentialFailureMessageService()

  def credentialFailureMessageRepository = Mock(
      CredentialFailureMessageRepository )

    def customErrorMessageService = new CustomErrorMessageService()

  def setup() {

    service.credentialFailureMessageRepository =
        credentialFailureMessageRepository
    service.defaultMessage = 'defaultMessage'
    service.customErrorMessageService=customErrorMessageService

  }

  def "invoking method successfully" () {

    when:

      def result = service.findByInstitutionAndMessage( credential, institution, message )
    then:
      1 * credentialFailureMessageRepository
          .findFirstByOriginalMessage(
            _ as String ) >>
          new CredentialFailureMessage( friendlyMessage: 'friendlyMessage' )
      result == 'friendlyMessage'
    where:
      credential = new Credential()
      institution = new FinancialInstitution()
      message = 'message'

  }

  def "instance not found" () {

    when:
      def result = service.findByInstitutionAndMessage( credential, institution, message )
    then:
      1 * credentialFailureMessageRepository
          .findFirstByOriginalMessage(
           _ as String )
      result == 'defaultMessage'
    where:
      credential = new Credential()
      institution = new FinancialInstitution()
      message = 'message'
     
  }

  def "parameter 'institution' is null" () {

    when:
      service.findByInstitutionAndMessage( credential, institution, message )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialFailureMessageService' +
          '.findByInstitutionAndMessage.institution.null'
    where:
      credential = new Credential()
      institution = null
      message = 'message'
 
  }

  def "parameter 'message' is null" () {

    when:
      service.findByInstitutionAndMessage( credential, institution, message )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialFailureMessageService' +
          '.findByInstitutionAndMessage.message.null'
    where:
      credential = new Credential()
      institution = new FinancialInstitution()
      message = null
     
  }

  def "parameter 'message' is blank" () {

    when:
      service.findByInstitutionAndMessage( credential, institution, message )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialFailureMessageService' +
          '.findByInstitutionAndMessage.message.null'
    where:
      credential = new Credential()   
      institution = new FinancialInstitution()
      message = ''
   
  }

}
