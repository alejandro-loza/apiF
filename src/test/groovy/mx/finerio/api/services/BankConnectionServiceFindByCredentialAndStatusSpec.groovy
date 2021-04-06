package mx.finerio.api.services

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.BankConnection
import mx.finerio.api.domain.repository.BankConnectionRepository
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class BankConnectionServiceFindByCredentialAndStatusSpec extends Specification {

  def service = new BankConnectionService()

  def bankConnectionRepository = Mock( BankConnectionRepository )

  def setup() {
    service.bankConnectionRepository = bankConnectionRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findByCredentialAndStatus( credential, status )
    then:
      1 * bankConnectionRepository.findFirstByCredentialAndStatus(
          _ as Credential, _ as BankConnection.Status ) >>
          new BankConnection()
      result instanceof BankConnection
    where:
      credential = new Credential()
      status = BankConnection.Status.PENDING

  }

  def "parameter 'credential' is null"() {

    when:
      service.findByCredentialAndStatus( credential, status )
    then:
      BadImplementationException e = thrown()
      e.message ==
          'bankConnectionService.findByCredentialAndStatus.credential.null'
    where:
      credential = null
      status = BankConnection.Status.PENDING

  }

  def "parameter 'status' is null"() {

    when:
      service.findByCredentialAndStatus( credential, status )
    then:
      BadImplementationException e = thrown()
      e.message ==
          'bankConnectionService.findByCredentialAndStatus.status.null'
    where:
      credential = new Credential()
      status = null

  }

  def "instance not found"() {

    when:
      service.findByCredentialAndStatus( credential, status )
    then:
      1 * bankConnectionRepository.findFirstByCredentialAndStatus(
          _ as Credential, _ as BankConnection.Status ) >> null
    where:
      credential = new Credential()
      status = BankConnection.Status.PENDING

  }

}
