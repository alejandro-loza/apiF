package mx.finerio.api.services

import mx.finerio.api.domain.Callback
import mx.finerio.api.dtos.TransactionDto
import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ScraperCallbackService {

  @Autowired
  CallbackService callbackService

  @Autowired
  CredentialService credentialService

  @Autowired
  MovementService movementService

  @Autowired
  TransactionService transactionService

  void processTransactions( TransactionDto transactionDto ) throws Exception {

    validateProcessTransactionsInput( transactionDto )
    def movements = movementService.createAll( transactionDto.data )
    def transactions = transactionService.createAll( transactionDto.data )
    def credential = credentialService.findAndValidate(
        transactionDto?.data?.credential_id as String )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.TRANSACTIONS, [ credentialId: credential.id,
        accountId: transactionDto.data.account_id ] )

    if ( movements ) {
      callbackService.sendToClient( credential?.customer?.client,
          Callback.Nature.NOTIFY, [ credentialId: credential.id,
          accountId: transactionDto.data.account_id,
          stage: 'categorize_transactions' ] )
    }

    movements.each { movementService.createConcept( it ) }
    transactions.each { transactionService.categorize( it ) }

  }

  private void validateProcessTransactionsInput(
      TransactionDto transactionDto ) throws Exception {

    if ( !transactionDto ) {
      throw new BadImplementationException(
          'scraperCallbackService.processTransactions.transactionDto.null' )
    }

  }

}
