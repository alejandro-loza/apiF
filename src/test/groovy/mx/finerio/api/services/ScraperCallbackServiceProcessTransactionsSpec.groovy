package mx.finerio.api.services

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.Movement
import mx.finerio.api.domain.Transaction
import mx.finerio.api.dtos.TransactionData
import mx.finerio.api.dtos.TransactionDto
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class ScraperCallbackServiceProcessTransactionsSpec extends Specification {

  def service = new ScraperCallbackService()

  def callbackService = Mock( CallbackService )
  def credentialService = Mock( CredentialService )
  def movementService = Mock( MovementService )
  def accountService = Mock( AccountService )
  def credentialStateService = Mock( CredentialStateService )
  def widgetEventsService = Mock( WidgetEventsService )

  def setup() {

    service.callbackService = callbackService
    service.credentialService = credentialService
    service.movementService = movementService
    service.accountService = accountService
    service.credentialStateService = credentialStateService
    service.widgetEventsService = widgetEventsService

  }

  def "everything was OK"() {

    when:
      service.processTransactions( transactionDto )
    then:
      1 * accountService.findByIdAndCredentialId(
              _ as String, _ as String ) >> new Account(id: "1")
      1 * movementService.createAll( _ as TransactionData ) >>
          [ new Movement(), new Movement() ]
      1 * credentialService.findAndValidate( _ as String ) >>
          new Credential( id: 'id', customer: new Customer(
          client: new Client( categorizeTransactions: true ) ) )
      1 * callbackService.sendToClient( _ as Client, _ as Callback.Nature,
          _ as Map )
    where:
      transactionDto = getTransactionDto()

  }

  def "client does not categorize transactions"() {

    when:
      service.processTransactions( transactionDto )
    then:
      1 * accountService.findByIdAndCredentialId(
            _ as String, _ as String ) >> new Account(id: "1")
      1 * movementService.createAll( _ as TransactionData ) >>
          [ new Movement(), new Movement() ]
      1 * credentialService.findAndValidate( _ as String ) >>
          new Credential( id: 'id', customer: new Customer(
          client: new Client( categorizeTransactions: false ) ) )
      1 * callbackService.sendToClient( _ as Client, _ as Callback.Nature,
          _ as Map )
    where:
      transactionDto = getTransactionDto()

  }

  def "No new movements"() {

    when:
      service.processTransactions( transactionDto )
    then:
      1 * accountService.findByIdAndCredentialId(
            _ as String, _ as String ) >> new Account(id: "1")
      1 * movementService.createAll( _ as TransactionData ) >> []
      1 * credentialService.findAndValidate( _ as String ) >>
          new Credential( id: 'id', customer: new Customer(
          client: new Client() ) )
      1 * callbackService.sendToClient( _ as Client, _ as Callback.Nature,
          _ as Map )
    where:
      transactionDto = getTransactionDto()

  }

  def "parameter 'transactionDto' is null"() {

    when:
      service.processTransactions( transactionDto )
    then:
      BadImplementationException e = thrown()
      e.message ==
          'scraperCallbackService.processTransactions.transactionDto.null'
    where:
      transactionDto = null

  }

  private TransactionDto getTransactionDto() throws Exception {

    return new TransactionDto(
      data: new TransactionData( credential_id: 'id', account_id: 'account_id' )
    )

  }

}
