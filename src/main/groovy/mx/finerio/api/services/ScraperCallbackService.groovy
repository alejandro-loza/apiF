package mx.finerio.api.services

import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.Transaction
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.dtos.SuccessCallbackDto
import mx.finerio.api.dtos.TransactionDto
import mx.finerio.api.dtos.WidgetEventsDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.threads.CategorizeTransactionThread
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import mx.finerio.api.services.AdminService.EntityType
import static mx.finerio.api.domain.FinancialInstitution.InstitutionType.PERSONAL

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
class ScraperCallbackService {

  @Value('${categorizer.parallel.threads}')
  int parallelCategorizeThreads

  @Autowired
  AccountService accountService

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

  List processTransactions( TransactionDto transactionDto ) throws Exception {

    validateProcessTransactionsInput( transactionDto )
    def credential = credentialService.findAndValidate(
        transactionDto?.data?.credential_id as String )
    def movements = validateTransactionsTableUsage( transactionDto, credential )
    def data = [:]
    data.customerId = credential?.customer?.id
    data.credentialId = credential.id
    def account = accountService.findByIdAndCredentialId(
        transactionDto.data.account_id, credential.id )
    data.accountId = account.id
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
    def data = [
      customerId: credential?.customer?.id,
      credentialId: credential.id
    ]
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
    def account = accountService.findByIdAndCredentialId(
        transactionDto.data.account_id, credential.id )
    def transactions = transactionService.createAll( transactionDto.data )
    if ( credential?.customer?.client?.categorizeTransactions
            && credential?.institution?.institutionType == PERSONAL ) {
      parallelCategorize(transactions)
      def data = [
        customerId: credential?.customer?.id,
        credentialId: credential.id,
        accountId: account.id,
        stage: 'categorize_transactions'
      ]
      credentialStateService.addState(credential.id, data)
      callbackService.sendToClient(credential?.customer?.client,
              Callback.Nature.NOTIFY, data)

    }
    return transactions

  }

  private void parallelCategorize( List transactions ) throws Exception {

    for ( int i = 0; i < transactions.size();
        i += parallelCategorizeThreads ) {

      def executorService = Executors.newFixedThreadPool(
          parallelCategorizeThreads )
      def maxLimit = i + parallelCategorizeThreads

      if ( maxLimit >= transactions.size() ) {
        maxLimit = transactions.size() - 1
      }

      def batch = transactions[ i..maxLimit ]

      for ( transaction in batch ) {
        def thread = new CategorizeTransactionThread()
        thread.transactionService = transactionService
        thread.transaction = transaction
        executorService.execute( thread )
      }

      executorService.shutdown()
      executorService.awaitTermination( 10, TimeUnit.MINUTES )

    }

  }

}

