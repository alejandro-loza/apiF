package mx.finerio.api.services


import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.microsoft.azure.servicebus.*
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder
import mx.finerio.api.dtos.TransactionDto
import java.util.concurrent.*
import groovy.json.JsonBuilder
import static java.nio.charset.StandardCharsets.*
import java.time.Duration
import org.springframework.beans.factory.annotation.Autowired
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value

@Service
class AzureQueueService {

  final static Logger log = LoggerFactory.getLogger(
    'mx.finerio.api.services.AzureQueueService' )
 
  final String serviceUrl
  final String serviceName
  final Integer timeToLive
  ConnectionStringBuilder connection
  QueueClient sendClient

  @Autowired
  public AzureQueueService (@Value('${servicebus.azure.transactions.stringConnection}') final String serviceUrl, 
    @Value('${servicebus.azure.transactions.name}') final String serviceName,
    @Value('${servicebus.azure.transactions.susbscriber.timeToLiveMinutes}') final String timeToLive ){

    this.serviceUrl = serviceUrl
    this.serviceName = serviceName
    this.timeToLive = timeToLive as Integer

    connection = new ConnectionStringBuilder( serviceUrl, serviceName )
    sendClient = new QueueClient( connection , ReceiveMode.PEEKLOCK )

  }

  void queueTransactions( TransactionDto transactionDto ) throws Exception {
          sendMessagesAsync( transactionDto )
  }

  private CompletableFuture<Void> sendMessagesAsync( TransactionDto transactionDto ) {

    List<CompletableFuture> tasks = []
    String randomNumber = "-" + ( Math.random() * 1000 ) as String 
    Message message = new Message( new JsonBuilder( transactionDto ).toPrettyString().getBytes( UTF_8) )

    message.setContentType("application/json")
    message.setLabel("transaction")
    message.setMessageId( ( new Date().getTime() as String ) + randomNumber );
    message.setTimeToLive(Duration.ofMinutes( timeToLive ))

    tasks.add(
      log.info( "Message sent to transactions queue: Id ${message.getMessageId()}" )
      sendClient.sendAsync(message).thenRunAsync( {
        log.info( "Message acknowledged in transactions queue: Id ${message.getMessageId()}" )
      }))  
        
    CompletableFuture.allOf(tasks.toArray( new CompletableFuture<?>[tasks.size()] ) )
  }


}
