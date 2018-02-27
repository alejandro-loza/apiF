package mx.finerio.api.services

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.AccountCredential
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.AccountCredentialRepository
import mx.finerio.api.domain.repository.AccountRepository
import mx.finerio.api.dtos.ListDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException

import org.springframework.data.jpa.repository.JpaRepository

import spock.lang.Specification

class AccountServiceFindAllSpec extends Specification {

  def service = new AccountService()

  def listService = Mock( ListService )
  def credentialService = Mock( CredentialService )
  def securityService = Mock( SecurityService )
  def accountCredentialRepository = Mock( AccountCredentialRepository )
  def accountRepository = Mock( AccountRepository )

  def setup() {

    service.listService = listService
    service.credentialService = credentialService
    service.securityService = securityService
    service.accountCredentialRepository = accountCredentialRepository
    service.accountRepository = accountRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findAll( params )
    then:
      1 * listService.validateFindAllDto( _ as ListDto, _ as Map )
      1 * securityService.getCurrent() >> client
      1 * accountCredentialRepository.findFirstByAccountId( _ as String ) >>
          new AccountCredential( account: new Account(),
          credential: new Credential( customer: new Customer(
          client: client ) ) )
      1 * credentialService.findOne( _ as String ) >> new Credential(
          customer: new Customer( client: client ) )
      1 * listService.findAll( _ as ListDto, _ as JpaRepository,
          _ as Object ) >> [ data:
          [ new AccountCredential( account: new Account() ),
          new AccountCredential( account: new Account() ) ],
          nextCursor: 'nextCursor' ]
      result instanceof Map
      result.nextCursor == 'nextCursor'
      result.data instanceof List
      result.data.size() == 2
      result.data[ 0 ] instanceof Account
    where:
      params = getParams()
      client = new Client( id: 1L )

  }

  def "parameter 'params' is null"() {

    when:
      service.findAll( params )
    then:
      BadImplementationException e = thrown()
      e.message == 'accountService.findAll.params.null'
    where:
      params = null

  }

  def "parameter 'params.cursor' is null"() {

    given:
      params.cursor = null
    when:
      def result = service.findAll( params )
    then:
      1 * listService.validateFindAllDto( _ as ListDto, _ as Map )
      1 * credentialService.findOne( _ as String ) >> new Credential()
      1 * listService.findAll( _ as ListDto, _ as JpaRepository,
          _ as Object ) >> [ data:
          [ new AccountCredential( account: new Account() ),
          new AccountCredential( account: new Account() ) ],
          nextCursor: 'nextCursor' ]
      result instanceof Map
      result.nextCursor == 'nextCursor'
      result.data instanceof List
      result.data.size() == 2
      result.data[ 0 ] instanceof Account
    where:
      params = getParams()

  }

  def "parameter 'params.credentialId' is null"() {

    given:
      params.credentialId = null
    when:
      service.findAll( params )
    then:
      BadRequestException e = thrown()
      e.message == 'account.findAll.credentialId.null'
    where:
      params = getParams()

  }

  private Map getParams() throws Exception {

    [
      credentialId: 'credentalId',
      cursor: 'cursor'
    ]

  }

}
