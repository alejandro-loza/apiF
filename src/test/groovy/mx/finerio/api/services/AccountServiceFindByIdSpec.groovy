package mx.finerio.api.services

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.repository.AccountRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class AccountServiceFindByIdSpec extends Specification {

  def service = new AccountService()

  def accountRepository = Mock( AccountRepository )

  def setup() {
    service.accountRepository = accountRepository
  }

  def "invoking method successfully"() {

    when:
      def result = service.findById( id )
    then:
      1 * accountRepository.findById( _ as String ) >>
          new Account()
      result instanceof Account
    where:
      id = 'uuid'

  }

  def "parameter 'id' is null"() {

    when:
      service.findById( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'accountService.findById.id.null'
    where:
      id = null

  }

  def "parameter 'id' is blank"() {

    when:
      service.findById( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'accountService.findById.id.null'
    where:
      id = ''

  }

  def "instance not found"() {

    when:
      service.findById( id )
    then:
      1 * accountRepository.findById( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'account.not.found'
    where:
      id = 'uuid'

  }

}
