package mx.finerio.api.services

import mx.finerio.api.domain.Transaction

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
  TransactionsAtmService transactionsAtmService

  @Autowired
  TransactionDuplicatedService transactionDuplicatedService

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
      executorService.execute( new CategorizerThread( transactionsAtmService,
              transactionDuplicatedService, movements[ i ] ) )
    }
    
    executorService.shutdown()
    executorService.awaitTermination( 5, TimeUnit.MINUTES )

  }
  
}

class CategorizerThread implements Runnable {

  TransactionsAtmService transactionsAtmService
  TransactionDuplicatedService transactionDuplicatedService
  def movement
  
  CategorizerThread( TransactionsAtmService transactionsAtmService,
                     TransactionDuplicatedService transactionDuplicatedService,
          Object movement ) {

    this.transactionDuplicatedService = transactionDuplicatedService
    this.transactionsAtmService = transactionsAtmService
    this.movement = movement

  }
  
  void run() {
    movement instanceof Movement
            ? transactionsAtmService.processMovement( movement )
            : transactionDuplicatedService.duplicatedTransaction( movement )
  }

}
