package mx.finerio.api.services

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.Movement
import mx.finerio.api.domain.repository.AccountRepository
import mx.finerio.api.domain.repository.MovementRepository

import spock.lang.Specification

class TransactionsAtmServiceProcessMovementSpec extends Specification {

  def service = new TransactionsAtmService()

  def transactionPostProcessorService = Mock( TransactionPostProcessorService )
  def transactionsApiService = Mock( TransactionsApiService )
  def accountRepository = Mock( AccountRepository )
  def movementRepository = Mock( MovementRepository )

  def setup() {

    service.transactionPostProcessorService = transactionPostProcessorService
    service.transactionsApiService = transactionsApiService
    service.accountRepository = accountRepository
    service.movementRepository = movementRepository

  }

  def "invoking method successfully"() {

    when:
      service.processMovement( movement )
    then:
      1 * transactionPostProcessorService.processDuplicated( _ as Movement ) >>
          new Movement( amount: 1.00, account: new Account( balance: 1.00 ) )
      1 * transactionPostProcessorService.updateTransference( _ as Movement )
      1 * transactionsApiService.findDuplicated( _ as Movement ) >> false
      1 * accountRepository.save( _ as Account )
      1 * movementRepository.save( _ as Movement )
    where:
      movement = new Movement()

  }

  def "atm movement is null"() {

    when:
      service.processMovement( movement )
    then:
      1 * transactionPostProcessorService.processDuplicated( _ as Movement )
      1 * transactionPostProcessorService.updateTransference( _ as Movement )
      1 * transactionsApiService.findDuplicated( _ as Movement ) >> false
      0 * accountRepository.save( _ as Account )
      0 * movementRepository.save( _ as Movement )
    where:
      movement = new Movement()

  }

  def "movement is duplicated"() {

    when:
      service.processMovement( movement )
    then:
      1 * transactionPostProcessorService.processDuplicated( _ as Movement ) >>
          new Movement( amount: 1.00, account: new Account( balance: 1.00 ) )
      1 * transactionPostProcessorService.updateTransference( _ as Movement )
      1 * transactionsApiService.findDuplicated( _ as Movement ) >> true
      0 * accountRepository.save( _ as Account )
      1 * movementRepository.save( _ as Movement )
    where:
      movement = new Movement()

  }

}
