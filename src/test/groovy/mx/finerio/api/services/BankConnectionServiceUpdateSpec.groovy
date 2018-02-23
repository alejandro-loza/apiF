package mx.finerio.api.services

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.BankConnection
import mx.finerio.api.domain.repository.BankConnectionRepository
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class BankConnectionServiceUpdateSpec extends Specification {

  def service = new BankConnectionService()

  def bankConnectionRepository = Mock( BankConnectionRepository )

  def setup() {
    service.bankConnectionRepository = bankConnectionRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.update( credential, status )
    then:
      1 * bankConnectionRepository.findFirstByCredentialAndStatus(
          _ as Credential, _ as BankConnection.Status ) >>
          new BankConnection()
      1 * bankConnectionRepository.save( _ as BankConnection ) >>
          new BankConnection()
      result instanceof BankConnection
      result.endDate != null
      result.status == status
    where:
      credential = new Credential()
      status = BankConnection.Status.SUCCESS

  }

  def "parameter 'credential' is null"() {

    when:
      service.update( credential, status )
    then:
      BadImplementationException e = thrown()
      e.message ==
          'bankConnectionService.update.credential.null'
    where:
      credential = null
      status = BankConnection.Status.PENDING

  }

  def "parameter 'status' is null"() {

    when:
      service.update( credential, status )
    then:
      BadImplementationException e = thrown()
      e.message ==
          'bankConnectionService.update.status.null'
    where:
      credential = new Credential()
      status = null

  }

  def "instance not found"() {

    when:
      service.update( credential, status )
    then:
      1 * bankConnectionRepository.findFirstByCredentialAndStatus(
          _ as Credential, _ as BankConnection.Status ) >> null
      BadImplementationException e = thrown()
      e.message == 'bankConnection.not.found'
    where:
      credential = new Credential()
      status = BankConnection.Status.SUCCESS

  }

}
