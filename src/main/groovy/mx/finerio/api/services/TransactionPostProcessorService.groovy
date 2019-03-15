package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.dtos.*
import mx.finerio.api.exceptions.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionPostProcessorService {

  @Autowired
  MovementService movementService

  @Autowired
  TransactionsApiService transactionsApiService

  @Autowired
  AccountRepository accountRepository
  
  @Autowired
  CategoryRepository categoryRepository
  
  @Autowired
  FinancialInstitutionRepository financialInstitutionRepository
  
  @Autowired
  MovementRepository movementRepository

  @Value('${categories.atm.id}')
  String atmId

  @Value('${categories.atm.transference.description}')
  String atmMovementDescription

  @Value('${categories.transference.id}')
  String transferenceId

  @Transactional
  void processDuplicated( Movement movement ) throws Exception {

    if ( !movement ) {
      throw new BadImplementationException(
          'transactionPostProcessor.processDuplicated.movement.null' )
    }

    def duplicated = false

    if ( movement.type == Movement.Type.DEPOSIT &&
        movement.account?.nature == "Cr\u00E9dito" ) {
      duplicated = true
    } else if ( movement.type == Movement.Type.CHARGE ) {

      if ( movement?.category?.id == atmId ) {
        if ( BigDecimal.ZERO.compareTo( 
              movement.amount.remainder(new BigDecimal(50)) ) == 0 ) {
          duplicated = true
        }
        processAtmWithdrawal( movement, duplicated )
      }

    }

    if ( duplicated ) { movementService.updateDuplicated( movement ) }

  }

  @Transactional
  void updateTransference( Movement mov ){
    
    def tp
    if( mov.type == Movement.Type.DEPOSIT ){ tp = Movement.Type.CHARGE }
    if( mov.type == Movement.Type.CHARGE ){ tp = Movement.Type.DEPOSIT }
    List movsTransf  = movementService.getMovementsToTransference( mov, tp )
      if( movsTransf ){
        def dateMinus = mov.date.minus( 5 )
        List list = movsTransf.findAll{ it.date <= mov.date && it.date >= dateMinus }
        if(list){
          def descriptions = []
          descriptions << mov.description
          descriptions += list.collect{ it.description }
          def params = [ list: descriptions, type: mov.type, bank: mov.account.institution.code ]
          def result = transactionsApiService.findTransference( params )
          def flag = result.findAll{  it.transference == true }
          if( flag ){
            def listMatch = list.findAll{ it.description.toLowerCase() in flag.description }
            listMatch.unique()
            listMatch.each{
              movementService.updateDuplicated( it )
            }
            movementService.updateDuplicated( mov )
          }
           
        }
      }

  }

  private void processAtmWithdrawal( Movement movement, Boolean duplicated )
      throws Exception {

    if ( movement.category?.id != atmId ||
        movement.type != Movement.Type.CHARGE ) {
      return
    }

    def atmAccount = getAtmAccount( movement.account.user )
    if ( atmAccount == null ) { return }
    def atmMovement = new Movement()
    atmMovement.account = atmAccount
    atmMovement.date = movement.date
    atmMovement.customDate = movement.customDate
    atmMovement.description = atmMovementDescription
    atmMovement.customDescription = atmMovementDescription
    atmMovement.amount = movement.amount
    atmMovement.balance = movement.balance
    atmMovement.type = Movement.Type.DEPOSIT
    atmMovement.dateCreated = movement.dateCreated
    atmMovement.lastUpdated = movement.lastUpdated
    atmMovement.category = categoryRepository.findOne( transferenceId )
    atmMovement.duplicated = duplicated
    movementRepository.save( atmMovement )
    
  }

  private Account getAtmAccount( User user ) throws Exception {

    def cashBank = financialInstitutionRepository.findById( 4L )
    def cashAccounts = accountRepository.findByUserAndInstitutionAndDateDeletedIsNull(
        user, cashBank )
    def atmAccount = cashAccounts.find{ it.nature?.contains( '_atm_d' ) }

    if ( atmAccount == null ) {
      atmAccount = cashAccounts.find{ it.nature?.contains( '_csh_d' ) }
    }
    
    if ( atmAccount == null ) {
      atmAccount = cashAccounts.find{ it.nature?.contains( 'ma_cash' ) }
    }

    return atmAccount
 
  }

}
