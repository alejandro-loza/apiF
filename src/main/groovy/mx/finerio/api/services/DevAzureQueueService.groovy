package mx.finerio.api.services

import mx.finerio.api.domain.TransactionMessageType
import mx.finerio.api.dtos.TransactionDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile('dev')
class DevAzureQueueService implements AzureQueueService {

  @Autowired
  ScraperCallbackService scraperCallbackService

  @Override
  void queueTransactions( TransactionDto transactionDto,
      TransactionMessageType type ) throws Exception {

    def messageType = type as TransactionMessageType

    switch ( messageType ) {
      case TransactionMessageType.CONTENT:
        def movements = scraperCallbackService.processTransactions(
            transactionDto )
        scraperCallbackService.processMovements( movements,
        transactionDto?.data?.credential_id as String )
        break
      case TransactionMessageType.END:
        def credential = scraperCallbackService.processSuccess( 
            SuccessCallbackDto.getInstanceFromCredentialId(
                transactionDto.data.credential_id ) )
        scraperCallbackService.postProcessSuccess( credential )
        break
    }

  }

}
