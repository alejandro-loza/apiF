package mx.finerio.api.services

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import mx.finerio.api.domain.Movement
import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TransactionCategorizerService {

  @Autowired
  MovementService movementService

  @Autowired
  TransactionPostProcessorService transactionPostProcessorService

  @Value('${categorizer.maxThreads}')
  Integer maxThreads
  
  void categorizeAll( List movements ) throws Exception {
  
    if ( movements == null ) {
      throw new BadImplementationException(
          'transactionCategorizerService.ctagorizeAll.movements.null' )
    }

    if ( movements.size() == 0 ) { return }
    def currentIndex = 0
    
    for ( int i = 0; i < movements.size(); i+= maxThreads ) {
    
      if ( i + maxThreads >= movements.size() ) {
        currentIndex = i
        break;
      }
      
      def currentMovements = movements[ i..i + maxThreads - 1 ]
      categorizeCurrentMovements( currentMovements )
      
    }
    
    categorizeCurrentMovements( movements[
        currentIndex..movements.size() - 1 ] )
    
  }

  private void categorizeCurrentMovements( List movements )
      throws Exception {
  
    def executorService = Executors.newCachedThreadPool()
    
    for ( Integer i = 0; i < movements.size(); i++ ) {
      executorService.execute( new CategorizerThread( movementService,
        transactionPostProcessorService, movements[ i ] ) )
    }
    
    executorService.shutdown()
    executorService.awaitTermination( 5, TimeUnit.MINUTES )

  }
  
}

class CategorizerThread implements Runnable {

  MovementService movementService
  TransactionPostProcessorService transactionPostProcessorService
  Movement movement
  
  CategorizerThread( MovementService movementService,
      TransactionPostProcessorService transactionPostProcessorService,
      Movement movement ) {

    this.movementService = movementService
    this.transactionPostProcessorService = transactionPostProcessorService
    this.movement = movement

  }
  
  void run() {

    movementService.createConcept( movement )
    transactionPostProcessorService.processDuplicated( movement )

  }

}
