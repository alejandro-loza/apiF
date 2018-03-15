package mx.finerio.api.services

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.Transaction
import mx.finerio.api.domain.repository.TransactionRepository
import mx.finerio.api.dtos.ListDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException

import org.springframework.data.jpa.repository.JpaRepository

import spock.lang.Specification

class TransactionServiceFindAllSpec extends Specification {

  def service = new TransactionService()

  def listService = Mock( ListService )
  def accountService = Mock( AccountService )
  def transactionRepository = Mock( TransactionRepository )

  def setup() {

    service.listService = listService
    service.accountService = accountService
    service.transactionRepository = transactionRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findAll( params )
    then:
      1 * listService.validateFindAllDto( _ as ListDto, _ as Map )
      1 * transactionRepository.findOne( _ as Long )>>
          new Transaction( account: new Account( id: 'id' ) )
      2 * accountService.findOne( _ as String ) >> new Account()
      1 * listService.findAll( _ as ListDto, _ as JpaRepository,
          _ as Object ) >> [ data: [ new Transaction(), new Transaction() ],
          nextCursor: 'nextCursor' ]
      result instanceof Map
      result.nextCursor == 'nextCursor'
      result.data instanceof List
      result.data.size() == 2
      result.data[ 0 ] instanceof Transaction
    where:
      params = getParams()

  }

  def "parameter 'params' is null"() {

    when:
      service.findAll( params )
    then:
      BadImplementationException e = thrown()
      e.message == 'transactionService.findAll.params.null'
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
      0 * transactionRepository.findOne( _ as Long )>>
          new Transaction( account: new Account( id: 'id' ) )
      1 * accountService.findOne( _ as String ) >> new Account( id: 'id' )
      1 * listService.findAll( _ as ListDto, _ as JpaRepository,
          _ as Object ) >> [ data: [ new Transaction(), new Transaction() ],
          nextCursor: 'nextCursor' ]
      result instanceof Map
      result.nextCursor == 'nextCursor'
      result.data instanceof List
      result.data.size() == 2
      result.data[ 0 ] instanceof Transaction
    where:
      params = getParams()

  }

  def "parameter 'params.cursor' is invalid"() {

    given:
      params.cursor = 'invalid'
    when:
      def result = service.findAll( params )
    then:
      1 * listService.validateFindAllDto( _ as ListDto, _ as Map )
      BadRequestException e = thrown()
      e.message == 'cursor.invalid'
    where:
      params = getParams()

  }

  def "parameter 'params.accountId' is null"() {

    given:
      params.accountId = null
    when:
      service.findAll( params )
    then:
      BadRequestException e = thrown()
      e.message == 'transaction.findAll.accountId.null'
    where:
      params = getParams()

  }

  private Map getParams() throws Exception {

    [
      accountId: '1',
      cursor: '1'
    ]

  }

}
