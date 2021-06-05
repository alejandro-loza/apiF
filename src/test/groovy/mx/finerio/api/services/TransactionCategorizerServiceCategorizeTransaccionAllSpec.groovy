package mx.finerio.api.services

import mx.finerio.api.domain.Movement
import mx.finerio.api.domain.Transaction
import mx.finerio.api.exceptions.BadImplementationException
import spock.lang.Specification

class TransactionCategorizerServiceCategorizeTransaccionAllSpec extends Specification {

  def service = new TransactionCategorizerService()
  def transactionDuplicatedService = Mock( TransactionDuplicatedService )

  def setup() {
    service.transactionDuplicatedService = transactionDuplicatedService
  }

  def "invoking method successfully"() {

    given:
      service.maxThreads = 5
    when:
      service.categorizeAll( transactions )
    then:
      true
    where:
      transactions = getTransactions()

  }

  def "parameter 'movements' is null"() {

    when:
      service.categorizeAll( transactions )
    then:
      BadImplementationException e = thrown()
      e.message ==
          'transactionCategorizerService.ctagorizeAll.movements.null'
    where:
      transactions = null

  }

  private List getTransactions() throws Exception {

    [
      getTransaction(),
      getTransaction(),
      getTransaction()
    ]

  }

  private Transaction getTransaction() throws Exception {
    new Transaction()
  }

}
