package mx.finerio.api.services

import mx.finerio.api.domain.Movement
import mx.finerio.api.domain.repository.AccountRepository
import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TransactionsAtmService {

  @Autowired
  TransactionsApiService transactionsApiService

  @Autowired
  TransactionPostProcessorService transactionPostProcessorService

  @Autowired
  AccountRepository accountRepository

  void processMovement( Movement movement ) throws Exception {

    def atmMovement = transactionPostProcessorService.processDuplicated(
        movement )
    transactionPostProcessorService.updateTransference( movement )
    def duplicated = transactionsApiService.findDuplicated( movement )

    if ( atmMovement != null && !duplicated ) {

      def atmAccount = atmMovement.account
      atmAccount.balance += atmMovement.amount
      accountRepository.save( atmAccount )

    }

  }

}
