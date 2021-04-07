package mx.finerio.api.threads

import mx.finerio.api.domain.Transaction
import mx.finerio.api.services.TransactionService

class CategorizeTransactionThread implements Runnable {

  TransactionService transactionService
    Transaction transaction

  void run() {
    transactionService.categorize(transaction)
  }

}