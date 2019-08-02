package mx.finerio.api.receiver

import mx.finerio.api.services.AzureQueueService
import com.microsoft.azure.servicebus.*
import mx.finerio.api.services.ScraperCallbackService
import org.springframework.beans.factory.InitializingBean
import java.time.Duration
import java.util.*
import java.util.concurrent.*
import java.util.function.Function
import org.springframework.beans.factory.annotation.Autowired
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder
import org.springframework.stereotype.Component
import groovy.json.JsonSlurper
import mx.finerio.api.dtos.*
import static java.nio.charset.StandardCharsets.*
import org.springframework.beans.factory.annotation.Value
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import mx.finerio.api.domain.TransactionMessageType
import javax.annotation.PreDestroy

@Component
class TransactionsReceiver implements InitializingBean {


  final static Logger log = LoggerFactory.getLogger(
    'mx.finerio.api.receiver.TransactionsReceiver' )	

	@Autowired
 	ScraperCallbackService scraperCallbackService

 	QueueClient queueClient
 	ExecutorService executorService
	final String serviceUrl
	final String serviceName 
  Integer maxConcurrentCalls
  Integer maxAutoRenewDuration 

    @Autowired
 	public TransactionsReceiver( @Value('${servicebus.azure.transactions.stringConnection}') final String serviceUrl, 
    @Value('${servicebus.azure.transactions.name}') final String serviceName,
    @Value('${servicebus.azure.transactions.receiver.maxConcurrentCalls}') final String maxConcurrentCalls,
    @Value('${servicebus.azure.transactions.receiver.maxAutoRenewDurationMinutes}') final String maxAutoRenewDuration  ){

 		this.serviceUrl = serviceUrl
    this.serviceName = serviceName
    this.maxConcurrentCalls = maxConcurrentCalls as Integer
    this.maxAutoRenewDuration = maxAutoRenewDuration as Integer
    
    queueClient = new QueueClient( new ConnectionStringBuilder( serviceUrl, serviceName ), ReceiveMode.PEEKLOCK )
   	executorService = Executors.newCachedThreadPool()

 	}

  	private  void registerReceiver() throws Exception {

    	queueClient.registerSessionHandler(
    		new ISessionHandler() {
                                       
	           public CompletableFuture<Void> onMessageAsync( IMessageSession session, IMessage message ) {
	        
   	       			log.info( "Transaction message received  with id: << Id:${message.getMessageId()}, sessionId:${message.getSessionId()}, type:${message.getLabel()}" )
	             
	               if ( message.label  && message.contentType &&
	                       message.contentType.contentEquals("application/json")) {

	                   	byte[] body = message.body
	                   	def transactionMap= [:]
               	  		transactionMap = new JsonSlurper().parseText( new String( body, UTF_8) )	            
	                    def transactionDto = getTransactionDtoFromMap( transactionMap )
                      processTransactionMessage( message.label, transactionDto )
                      log.info( "Transaction message processed successfully id: << Id:${message.getMessageId()}, sessionId:${message.getSessionId()}, type:${message.getLabel()}" )
	                 	
	               }

	               return CompletableFuture.completedFuture(null)
	           }

 	           public void notifyException( Throwable throwable, ExceptionPhase exceptionPhase ) {
                 log.info( "Notify Exception on message << ${throwable.getMessage() }" )
	     
	           }
             public CompletableFuture<Void> OnCloseSessionAsync( IMessageSession session ){
                 log.info( "Session closed with SessionId:  ${session.getSessionId()}" )
             }
           },
    
    new SessionHandlerOptions( maxConcurrentCalls, true, Duration.ofMinutes( maxAutoRenewDuration )), executorService)
    }


    private void processTransactionMessage( String type, TransactionDto transactionDto ){

      def messageType = type as TransactionMessageType
     switch ( messageType ) {
       case TransactionMessageType.START:
          log.info( "Message of type START received credentialId: ${transactionDto.data.credential_id} " )
       break
       case TransactionMessageType.CONTENT:
          log.info( "Message of type CONTENT received credentialId: ${transactionDto.data.credential_id}" )
          def movements = scraperCallbackService.processTransactions( transactionDto )
          scraperCallbackService.processMovements( movements )
       break
       case TransactionMessageType.END:
          log.info( "Message of type END received credentialId: ${transactionDto.data.credential_id}" )
          def credential = scraperCallbackService.processSuccess( 
          SuccessCallbackDto.getInstanceFromCredentialId( transactionDto.data.credential_id ) )
          scraperCallbackService.postProcessSuccess( credential )
       break;   
      }

    }

    private TransactionDto getTransactionDtoFromMap( transactionMap ){

    	def transactionDto = new TransactionDto()
        def transactionData = new TransactionData()
        def transactionList = []

        transactionMap.data.transactions.each{ 

        	def transaction = new Transaction()
        	transaction.made_on = it.made_on
        	transaction.description = it.description  
        	transaction.amount = it.amount
          def tExtraData = new TransactionExtraData() 
          tExtraData.transaction_Id = it.extra_data?.transaction_Id
          transaction.extra_data = tExtraData
          transactionList.add( transaction )

        }

        transactionData.transactions = transactionList
        transactionData.credential_id = transactionMap.data.credential_id
        transactionData.account_id = transactionMap.data.account_id
        transactionDto.data = transactionData
        transactionDto.meta = transactionMap.meta

        transactionDto
    }

    @Override
    public void afterPropertiesSet() throws Exception {
       this.registerReceiver()
       log.info( "-------- Transaction azure receiver registered -----------" )
    }

    @PreDestroy
    public void OnDestroy() {
      queueClient.close()
      executorService.shutdown()
      log.info('Queue client of transaction queue and executorService closed')
    }

}

