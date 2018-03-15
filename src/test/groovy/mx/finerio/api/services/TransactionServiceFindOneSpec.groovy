package mx.finerio.api.services

import java.sql.Timestamp

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.Transaction
import mx.finerio.api.domain.repository.TransactionRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class TransactionServiceFindOneSpec extends Specification {

  def service = new TransactionService()

  def accountService = Mock( AccountService )
  def transactionRepository = Mock( TransactionRepository )

  def setup() {

    service.accountService = accountService
    service.transactionRepository = transactionRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findOne( id )
    then:
      1 * accountService.findOne( _ as String ) >> account
      1 * transactionRepository.findOne( _ as Long ) >>
          new Transaction( account: account )
      result instanceof Transaction
    where:
      id = 1L
      account = new Account( id: 'id' )

  }

  def "parameter 'id' is null"() {

    when:
      service.findOne( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'transactionService.findOne.id.null'
    where:
      id = null

  }

  def "instance not found"() {

    when:
      service.findOne( id )
    then:
      1 * transactionRepository.findOne( _ as Long ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'transaction.not.found'
    where:
      id = 1L

  }

  def "instance not found (account not found)"() {

    when:
      service.findOne( id )
    then:
      1 * transactionRepository.findOne( _ as Long ) >>
          new Transaction( account: account )
      1 * accountService.findOne( _ as String ) >> {
          throw new InstanceNotFoundException( 'error' ) }
      InstanceNotFoundException e = thrown()
      e.message == 'transaction.not.found'
    where:
      id = 1L
      account = new Account( id: 'id' )

  }

  def "instance not found (dateDeleted is not null)"() {

    when:
      service.findOne( id )
    then:
      1 * transactionRepository.findOne( _ as Long ) >>
          new Transaction( dateDeleted: new Timestamp( new Date().time ) )
      InstanceNotFoundException e = thrown()
      e.message == 'transaction.not.found'
    where:
      id = 1L

  }

}
