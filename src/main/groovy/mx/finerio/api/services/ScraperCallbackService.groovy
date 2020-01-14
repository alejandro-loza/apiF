package mx.finerio.api.services

import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.Credential
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.dtos.SuccessCallbackDto
import mx.finerio.api.dtos.TransactionDto
import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ScraperCallbackService {

  @Autowired
  CallbackService callbackService

  @Autowired
  CredentialService credentialService

  @Autowired
  MovementService movementService

  @Autowired
  ScraperWebSocketService scraperWebSocketService

  @Autowired
  TransactionService transactionService

  @Autowired
  TransactionCategorizerService transactionCategorizerService

  @Autowired
  CredentialStatusHistoryService credentialStatusHistoryService
  
  @Transactional
  List processTransactions( TransactionDto transactionDto ) throws Exception {

    validateProcessTransactionsInput( transactionDto )
    def credential = credentialService.findAndValidate(
        transactionDto?.data?.credential_id as String )
    def movements = validateTransactionsTableUsage( transactionDto, credential )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.TRANSACTIONS, [ credentialId: credential.id,
        accountId: transactionDto.data.account_id ] )
    movements

  }

  @Transactional
  void processMovements( List movements, String credentialId ) throws Exception {
    def credential = credentialService.findAndValidate( credentialId )
    if ( !credential?.customer?.client?.useTransactionsTable ) { return } 
    transactionCategorizerService.categorizeAll( movements )
  }

  @Transactional
  Credential processSuccess( SuccessCallbackDto successCallbackDto )
      throws Exception {

    if ( !successCallbackDto ) {
      throw new BadImplementationException(
          'scraperCallbackService.processSuccess.successCallbackDto.null' )
    }

    def credential = credentialService.updateStatus(
        successCallbackDto?.data?.credential_id, Credential.Status.ACTIVE )
    credentialStatusHistoryService.update( credential )

    credential
  }

  void postProcessSuccess( Credential credential ) throws Exception {
    closeWebSocketSession( credential )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.SUCCESS, [ credentialId: credential.id ] )
  }

  @Transactional
  void processFailure( FailureCallbackDto failureCallbackDto ) throws Exception {

    if ( !failureCallbackDto ) {
      throw new BadImplementationException(
          'scraperCallbackService.processFailure.failureCallbackDto.null' )
    }

    def strStatusCode=String.valueOf( failureCallbackDto?.data?.status_code )
    def credential = credentialService.setFailure(
        failureCallbackDto?.data?.credential_id, strStatusCode )
    credentialStatusHistoryService.update( credential )
    closeWebSocketSession( credential )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.FAILURE, [ credentialId: credential.id,
        message: credential.errorCode, code: strStatusCode ] )

  }

     

  private void validateProcessTransactionsInput(
      TransactionDto transactionDto ) throws Exception {

    if ( !transactionDto ) {
      throw new BadImplementationException(
          'scraperCallbackService.processTransactions.transactionDto.null' )
    }

  }

  private void closeWebSocketSession( Credential credential )
      throws Exception {

    if ( credential.institution.code != 'BBVA' ) {
      return
    }

    scraperWebSocketService.closeSession( credential.id )

  }

  private List validateTransactionsTableUsage( TransactionDto transactionDto,
      Credential credential ) throws Exception {

    if ( !credential?.customer?.client?.useTransactionsTable ) { 
      return movementService.createAll( transactionDto.data )
    }
    def transactions = transactionService.createAll( transactionDto.data )

    if ( credential?.customer?.client?.categorizeTransactions ) {

      transactions.each { transactionService.categorize( it ) }
      callbackService.sendToClient( credential?.customer?.client,
          Callback.Nature.NOTIFY, [ credentialId: credential.id,
          accountId: transactionDto.data.account_id,
          stage: 'categorize_transactions' ] )

    }
    return transactions

  }

}

