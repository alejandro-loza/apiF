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

  @Value('${categories.atm.id}')
  String atmId

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

      def concept = conceptService.findByMovement( movement )

      if ( concept?.category?.id == atmId ) {
        duplicated = true
      }

    }

    if ( duplicated ) { movementService.updateDuplicated( movement ) }

  }

}
