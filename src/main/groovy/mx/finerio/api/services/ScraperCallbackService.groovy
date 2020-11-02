package mx.finerio.api.services

import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Transaction
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.dtos.SuccessCallbackDto
import mx.finerio.api.dtos.TransactionDto
import mx.finerio.api.dtos.WidgetEventsDto
import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import mx.finerio.api.services.AdminService.EntityType

@Service
class ScraperCallbackService {

  @Autowired
  CallbackService callbackService

  @Autowired
  CredentialFailureService credentialFailureService

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

  @Autowired
  AdminService adminService

  @Autowired
  CredentialStateService credentialStateService
  
  @Autowired
  WidgetEventsService widgetEventsService

  @Transactional
  List processTransactions( TransactionDto transactionDto ) throws Exception {

    validateProcessTransactionsInput( transactionDto )
    def credential = credentialService.findAndValidate(
        transactionDto?.data?.credential_id as String )
    def movements = validateTransactionsTableUsage( transactionDto, credential )
    def data = [:]
    data.credentialId = credential.id
    data.accountId = transactionDto.data.account_id
    data.transactions = []

    if ( !movements.isEmpty() && movements[ 0 ] instanceof Transaction ) {
      data.transactions = movements.collect {
        transactionService.getFields( it )
      }
    }
    credentialStateService.addState( credential.id, data )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.TRANSACTIONS, data )
    widgetEventsService.onTransactionsCreated( new WidgetEventsDto(
        credentialId: credential.id, accountId: data.accountId ) )
    movements

  }

  @Transactional
  void processMovements( List movements, String credentialId ) throws Exception {
    def credential = credentialService.findAndValidate( credentialId )
    if ( credential?.customer?.client?.useTransactionsTable ) { return }
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
    adminService.sendDataToAdmin( EntityType.CONNECTION, Boolean.valueOf(true), credential )
    credential
  }

  void postProcessSuccess( Credential credential ) throws Exception {
    closeWebSocketSession( credential )
    def data = [ credentialId: credential.id ]
    credentialStateService.addState( credential.id, data )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.SUCCESS, data )
    widgetEventsService.onSuccess( new WidgetEventsDto(
        credentialId: credential.id ) )
  }

  @Transactional
  void processFailure( FailureCallbackDto failureCallbackDto ) throws Exception {

    if ( !failureCallbackDto ) {
      throw new BadImplementationException(
          'scraperCallbackService.processFailure.failureCallbackDto.null' )
    }

    credentialFailureService.processFailure( failureCallbackDto )
    def credential = credentialService.findAndValidate(
        failureCallbackDto?.data?.credential_id as String )
    closeWebSocketSession( credential )

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
      def data = [ credentialId: credential.id,
          accountId: transactionDto.data.account_id,
          stage: 'categorize_transactions' ]
      credentialStateService.addState( credential.id, data )
      callbackService.sendToClient( credential?.customer?.client,
          Callback.Nature.NOTIFY, data )

    }
    return transactions

  }

}

