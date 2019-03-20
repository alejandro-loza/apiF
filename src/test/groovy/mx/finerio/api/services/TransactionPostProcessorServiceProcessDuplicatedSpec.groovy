package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.exceptions.*

import spock.lang.Specification

class TransactionPostProcessorServiceProcessDuplicatedSpec extends Specification {

  def service = new TransactionPostProcessorService()
  def accountRepository = Mock( AccountRepository )
  def categoryRepository = Mock( CategoryRepository )
  def financialInstitutionRepository = Mock( FinancialInstitutionRepository )
  def movementRepository = Mock( MovementRepository )

  def movementService = Mock( MovementService )

  def setup() {

    service.movementService = movementService
    service.accountRepository = accountRepository
    service.categoryRepository = categoryRepository
    service.financialInstitutionRepository = financialInstitutionRepository
    service.movementRepository = movementRepository

  }

  def "invoking method successfully (credit card income)"() {

    when:
      service.processDuplicated( movement )
    then:
      1 * movementService.updateDuplicated( _ as Movement )
    where:
      movement = new Movement( type: Movement.Type.DEPOSIT,
          account: new Account( nature: "Cr\u00E9dito" ) )

  }

  def "invoking method successfully (atm outcome)"() {

    given:
      service.atmId = atmId
    when:
      service.processDuplicated( movement )
    then:
      1 * movementService.updateDuplicated( _ as Movement )
      1 * financialInstitutionRepository.findById( _ as Long ) >>
          new FinancialInstitution()
      1 * accountRepository.findByUserAndInstitutionAndDateDeletedIsNull(
          _ as User, _ as FinancialInstitution ) >> [ getManualAccount( 'debit' ) ]
    where:
      movement = new Movement( type: Movement.Type.CHARGE,
      amount: 100.00, category: getCategory( 'atmId' ),
      account: new Account( nature: "Cr\u00E9dito", user: new User() ) )
      atmId = 'atmId'

  }

  def "invoking method successfully (atm outcome, atm default account)"() {

    given:
      service.atmId = atmId
      service.transferenceId = 'transferenceId'
    when:
      service.processDuplicated( movement )
    then:
      1 * movementService.updateDuplicated( _ as Movement )
      1 * financialInstitutionRepository.findById( _ as Long ) >>
          new FinancialInstitution()
      1 * accountRepository.findByUserAndInstitutionAndDateDeletedIsNull(
          _ as User, _ as FinancialInstitution ) >> [ getManualAccount( '_atm_d' ) ]
      1 * movementRepository.save( _ as Movement )
    where:
      movement = new Movement( type: Movement.Type.CHARGE,
      amount: 100.00, category: getCategory( 'atmId' ),
      account: new Account( nature: "Cr\u00E9dito", user: new User() ) )
      atmId = 'atmId'

  }

  def "invoking method successfully (atm outcome, cash default account)"() {

    given:
      service.atmId = atmId
      service.transferenceId = 'transferenceId'
    when:
      service.processDuplicated( movement )
    then:
      1 * movementService.updateDuplicated( _ as Movement )
      1 * financialInstitutionRepository.findById( _ as Long ) >>
          new FinancialInstitution()
      1 * accountRepository.findByUserAndInstitutionAndDateDeletedIsNull(
          _ as User, _ as FinancialInstitution ) >> [ getManualAccount( '_csh_d' ) ]
      1 * movementRepository.save( _ as Movement )
    where:
      movement = new Movement( type: Movement.Type.CHARGE,
      amount: 100.00, category: getCategory( 'atmId' ),
      account: new Account( nature: "Cr\u00E9dito", user: new User() ) )
      atmId = 'atmId'

  }

  def "invoking method successfully (atm outcome, cash account)"() {

    given:
      service.atmId = atmId
      service.transferenceId = 'transferenceId'
    when:
      service.processDuplicated( movement )
    then:
      1 * movementService.updateDuplicated( _ as Movement )
      1 * financialInstitutionRepository.findById( _ as Long ) >>
          new FinancialInstitution()
      1 * accountRepository.findByUserAndInstitutionAndDateDeletedIsNull(
          _ as User, _ as FinancialInstitution ) >> [ getManualAccount( 'ma_cash' ) ]
      1 * movementRepository.save( _ as Movement )
    where:
      movement = new Movement( type: Movement.Type.CHARGE,
      amount: 100.00, category: getCategory( 'atmId' ),
      account: new Account( nature: "Cr\u00E9dito", user: new User() ) )
      atmId = 'atmId'

  }

  def "invoking method successfully (not duplicated)"() {

    when:
      service.processDuplicated( movement )
    then:
      0 * movementService.updateDuplicated( _ as Movement )
    where:
      movement = new Movement()

  }

  def "parameter 'movement' is null"() {

    when:
      service.processDuplicated( movement )
    then:
      BadImplementationException e = thrown()
      e.message == 'transactionPostProcessor.processDuplicated.movement.null'
    where:
      movement = null

  }

  private Category getCategory( String id ) throws Exception {

    def category = new Category()
    category.id = id
    category

  }

  private Account getManualAccount( String nature ) throws Exception {

    def account = new Account()
    account.nature = nature
    return account

  }

}
