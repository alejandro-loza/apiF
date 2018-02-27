package mx.finerio.api.services

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.AccountCredential
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.AccountCredentialRepository
import mx.finerio.api.dtos.AccountDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class AccountServiceFindOneSpec extends Specification {

  def service = new AccountService()

  def securityService = Mock( SecurityService )
  def accountCredentialRepository = Mock( AccountCredentialRepository )

  def setup() {

    service.securityService = securityService
    service.accountCredentialRepository = accountCredentialRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findOne( id )
    then:
      1 * securityService.getCurrent() >> client
      1 * accountCredentialRepository.findFirstByAccountId( _ as String ) >>
          new AccountCredential( account: new Account(),
          credential: new Credential( customer: new Customer(
          client: client ) ) )
      result instanceof Account
    where:
      id = 'uuid'
      client = new Client( id: 1 )

  }

  def "parameter 'id' is null"() {

    when:
      service.findOne( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'accountService.findOne.id.null'
    where:
      id = null

  }

  def "instance not found"() {

    when:
      service.findOne( id )
    then:
      1 * securityService.getCurrent() >> client
      1 * accountCredentialRepository.findFirstByAccountId( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'account.not.found'
    where:
      id = 'uuid'
      client = new Client( id: 1 )

  }

  def "instance not found (different client)"() {

    when:
      service.findOne( id )
    then:
      1 * securityService.getCurrent() >> new Client( id: 2 )
      1 * accountCredentialRepository.findFirstByAccountId( _ as String ) >>
          new AccountCredential( account: new Account(),
          credential: new Credential( customer: new Customer(
          client: client ) ) )
      InstanceNotFoundException e = thrown()
      e.message == 'account.not.found'
    where:
      id = 'uuid'
      client = new Client( id: 1 )

  }

  def "instance not found (dateDeleted is not null)"() {

    when:
      service.findOne( id )
    then:
      1 * securityService.getCurrent() >> new Client( id: 1 )
      1 * accountCredentialRepository.findFirstByAccountId( _ as String ) >>
          new AccountCredential( account: new Account(
          dateDeleted: new Date() ),
          credential: new Credential( customer: new Customer(
          client: client ) ) )
      InstanceNotFoundException e = thrown()
      e.message == 'account.not.found'
    where:
      id = 'uuid'
      client = new Client( id: 1 )

  }

}
