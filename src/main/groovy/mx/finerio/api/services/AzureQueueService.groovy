package mx.finerio.api.services

import mx.finerio.api.domain.TransactionMessageType
import mx.finerio.api.dtos.TransactionDto

interface AzureQueueService {

  void queueTransactions( TransactionDto transactionDto,
      TransactionMessageType type ) throws Exception

}
