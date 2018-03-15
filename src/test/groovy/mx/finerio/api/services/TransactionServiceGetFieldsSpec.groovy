package mx.finerio.api.services

import java.sql.Timestamp

import mx.finerio.api.domain.Transaction
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class TransactionServiceGetFieldsSpec extends Specification {

  def service = new TransactionService()

  def "invoking method successfully"() {

    when:
      def result = service.getFields( transaction )
    then:
      result instanceof Map
      result.id != null
      result.description != null
      result.amount != null
      result.isCharge != null
      result.date != null
    where:
      transaction = getTransaction()

  }

  def "parameter 'transaction' is null"() {

    when:
      service.getFields( transaction )
    then:
      BadImplementationException e = thrown()
      e.message == 'transactionService.getFields.transaction.null'
    where:
      transaction = null

  }

  private Transaction getTransaction() throws Exception {

    new Transaction(
      id: 1L,
      description: 'description',
      amount: 1.00,
      charge: true,
      bankDate: new Timestamp(new Date().time )
    )

  }

}
