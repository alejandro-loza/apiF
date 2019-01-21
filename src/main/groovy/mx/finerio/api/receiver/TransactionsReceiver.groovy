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

    @Autowired
 	public TransactionsReceiver( @Value('${servicebus.azure.transactions.url}') final String serviceUrl, 
    @Value('${servicebus.azure.transactions.name}') final String serviceName ){

 		this.serviceUrl = serviceUrl
    	this.serviceName = serviceName

 		queueClient = new QueueClient(new ConnectionStringBuilder
      	( serviceUrl, serviceName ), ReceiveMode.RECEIVEANDDELETE)
   		executorService = Executors.newSingleThreadExecutor()

 	}

  	private  void registerReceiver() throws Exception {

    	queueClient.registerMessageHandler(
    		new IMessageHandler() {
                                       
	           public CompletableFuture<Void> onMessageAsync(IMessage message) {
	        
   	       			log.info( "Transaction message received  with id: ${message.getMessageId()}" )
	             
	               if (message.getLabel() != null &&
	                       message.getContentType() != null &&
	                       message.getLabel().contentEquals("transaction") &&
	                       message.getContentType().contentEquals("application/json")) {

	                   	byte[] body = message.getBody()
	                   	def transactionMap= [:]
               	  		transactionMap = new JsonSlurper().parseText( new String( body, UTF_8) )	            
	                    def transactionDto = getTransactionDtoFromMap( transactionMap )
	                 	scraperCallbackService.processTransactions(transactionDto)
	               }

	               return CompletableFuture.completedFuture(null)
	           }

	           public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
	               log.error( "${exceptionPhase}  ${throwable.getMessage()}" )
	           }
           },
    
    new MessageHandlerOptions(1, true, Duration.ofMinutes(10)),executorService)
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

}

