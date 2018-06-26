package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.dtos.*
import mx.finerio.api.exceptions.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionPostProcessorService {

  @Autowired
  MovementService movementService

  @Autowired
  ConceptService conceptService

  Movement processDuplicated( Movement movement ) throws Exception {

    if ( !movement || !movement.id) {
      throw new BadRequestException( 'transactionPostProcessor.processDuplicated.movement.null' )
    }
    def mov  = movementService.findOne( movement.id )
    def concept  = conceptService.findByMovement( movement )
    if( mov.account.nature == "Cr\u00E9dito" ){
      if( mov.type == Movement.Type.DEPOSIT ){
        mov = movementService.updateDuplicated( mov )
        return mov  
      }
    }
    mov
  }

}
