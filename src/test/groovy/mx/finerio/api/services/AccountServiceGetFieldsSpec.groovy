package mx.finerio.api.services

import mx.finerio.api.domain.Account
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class AccountServiceGetFieldsSpec extends Specification {

  def service = new AccountService()

  def "invoking method successfully"() {

    when:
      def result = service.getFields( account )
    then:
      result instanceof Map
      result.id != null
      result.name != null
      result.number != null
      result.balance != null
      result.type != null
      result.dateCreated != null
    where:
      account = getAccount()

  }

  def "parameter 'account' is null"() {

    when:
      service.getFields( account )
    then:
      BadImplementationException e = thrown()
      e.message == 'accountService.getFields.account.null'
    where:
      account = null

  }

  private Account getAccount() throws Exception {

    new Account(
      id: 1L,
      name: 'name',
      number: 'number',
      balance: 1.00,
      nature: 'nature',
      dateCreated: new Date()
    )

  }

}
