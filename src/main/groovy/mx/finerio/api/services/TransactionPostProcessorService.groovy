package mx.finerio.api.services

import mx.finerio.api.domain.*
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
  ConceptService conceptService

  @Autowired
  TransactionsApiService transactionsApiService

  @Value('${categories.atm.id}')
  String atmId

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
        duplicated = true
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


}
