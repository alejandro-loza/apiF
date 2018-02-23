package mx.finerio.api.services

import mx.finerio.api.domain.BankConnection
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.repository.BankConnectionRepository
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class BankConnectionServiceCreateSpec extends Specification {

  def service = new BankConnectionService()
  def bankConnectionRepository = Mock( BankConnectionRepository )

  def setup() {
    service.bankConnectionRepository = bankConnectionRepository
  }

  def "invoking method successfully"() {

    when:
      def result = service.create( credential )
    then:
      1 * bankConnectionRepository.save( _ as BankConnection ) >> new BankConnection()
      result instanceof BankConnection
    where:
      credential = new Credential()

  }

  def "parameter 'credential' is null"() {

    when:
      service.create( credential )
    then:
      BadImplementationException e = thrown()
      e.message == 'bankConnectionService.create.credential.null'
    where:
      credential = null

  }

}
