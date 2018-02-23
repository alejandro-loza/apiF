package mx.finerio.api.services

import mx.finerio.api.domain.BankConnection
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceSetFailureSpec extends Specification {

  def service = new CredentialService()

  def credentialRepository = Mock( CredentialRepository )
  def bankConnectionService = Mock( BankConnectionService )
  def credentialFailureMessageService = Mock( CredentialFailureMessageService )

  def setup() {

    service.bankConnectionService = bankConnectionService
    service.credentialFailureMessageService = credentialFailureMessageService
    service.credentialRepository = credentialRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.setFailure( credentialId, message )
    then:
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ),
          institution: new FinancialInstitution(),
          user: new User() )
      1 * credentialFailureMessageService.
          findByInstitutionAndMessage( _ as FinancialInstitution,
          _ as String )
      1 * credentialRepository.save( _ as Credential ) >>
          new Credential()
      1 * bankConnectionService.update( _ as Credential,
          _ as BankConnection.Status )
      result instanceof Credential
    where:
      credentialId = UUID.randomUUID().toString()
      message = 'message'
      client = new Client( id: 1 )

  }

  def "parameter 'credentialId' is null"() {

    when:
      service.setFailure( credentialId, message )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findAndValidate.id.null'
    where:
      credentialId = null
      message = 'message'

  }

  def "parameter 'credentialId' is empty"() {

    when:
      service.setFailure( credentialId, message )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findAndValidate.id.null'
    where:
      credentialId = ''
      message = 'message'

  }

  def "parameter 'credentialId' is invalid"() {

    when:
      service.setFailure( credentialId, message )
    then:
      InstanceNotFoundException e = thrown()
      e.message == 'credential.not.found'
    where:
      credentialId = UUID.randomUUID().toString()
      message = 'message'

  }

  def "parameter 'message' is null"() {

    when:
      service.setFailure( credentialId, message )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.setFailure.message.null'
    where:
      credentialId = UUID.randomUUID().toString()
      message = null

  }

  def "parameter 'message' is blank"() {

    when:
      def result = service.setFailure( credentialId, message )
    then:
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ),
          institution: new FinancialInstitution(),
          user: new User() )
      1 * credentialFailureMessageService.
          findByInstitutionAndMessage( _ as FinancialInstitution,
          _ as String )
      1 * credentialRepository.save( _ as Credential ) >>
          new Credential()
      result instanceof Credential
    where:
      credentialId = UUID.randomUUID().toString()
      message = ''
      client = new Client( id: 1 )

  }

}
