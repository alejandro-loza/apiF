package mx.finerio.api.services

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.AccountCredential
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.AccountCredentialRepository
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class AccountServiceGetFieldsSpec extends Specification {

  def service = new AccountService()
  def accountCredentialRepository = Mock( AccountCredentialRepository )

  def setup() {
    service.accountCredentialRepository = accountCredentialRepository;
  }

  def "invoking method successfully"() {

    when:
      def result = service.getFields( account )
    then:
      accountCredentialRepository.findFirstByAccountId(_ as String) >> new AccountCredential(id: 1L,
              account: account,
              credential: credential,
              version: 1L,
              lastUpdated: new Date(),
              dateCreated: new Date())
      result instanceof Map
      result.id != null
      result.name != null
      result.number != null
      result.balance != null
      result.type != null
      result.dateCreated != null
    where:
      account = getAccount()
      credential = new Credential()

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
      dateCreated: new Date(),
      institution: getFinancialInstitution()
    )

  }

  private FinancialInstitution getFinancialInstitution() throws Exception {

    new FinancialInstitution(
            id: 1L,
            name: 'name'
    )

  }

}
