package mx.finerio.api.services

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.BankConnection
import mx.finerio.api.domain.repository.BankConnectionRepository
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class BankConnectionServiceFindLastSpec extends Specification {

  def service = new BankConnectionService()

  def bankConnectionRepository = Mock( BankConnectionRepository )

  def setup() {
    service.bankConnectionRepository = bankConnectionRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findLast( credential )
    then:
      1 * bankConnectionRepository.findFirstByCredentialOrderByStartDateDesc(
          _ as Credential ) >>
          new BankConnection()
      result instanceof BankConnection
    where:
      credential = new Credential()

  }

  def "parameter 'credential' is null"() {

    when:
      service.findLast( credential )
    then:
      BadImplementationException e = thrown()
      e.message ==
          'bankConnectionService.findLast.credential.null'
    where:
      credential = null

  }

  def "instance not found"() {

    when:
      def result = service.findLast( credential )
    then:
      1 * bankConnectionRepository.findFirstByCredentialOrderByStartDateDesc(
          _ as Credential ) >> null
    result == null
    where:
      credential = new Credential()

  }

}
