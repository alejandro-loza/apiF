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
import javax.annotation.PreDestroy
import mx.finerio.api.exceptions.BadImplementationException

@Service
class AdminQueueService {

    enum AdminTopic {
      CREATE_CUSTOMER, CREATE_ACCOUNT, CREATE_CONNECTION, CREATE_CREDENTIAL, CREATE_MOVEMENT
    }

  final static Logger log = LoggerFactory.getLogger(
    'mx.finerio.api.services.AdminQueueService' )
 
  final String serviceUrl
  final String serviceName
  final Integer timeToLive
  ConnectionStringBuilder connection
  QueueClient sendClient
  Boolean isProduction

  @Autowired
  public AdminQueueService (
    @Value('${servicebus.azure.admin.stringConnection}') final String serviceUrl, 
    @Value('${servicebus.azure.admin.name}') final String serviceName,
    @Value('${admin.config.isProduction}') final Boolean isProduction,
    @Value('${servicebus.azure.admin.susbscriber.timeToLiveMinutes}') final String timeToLive ){

    this.serviceUrl = serviceUrl
    this.serviceName = serviceName
    this.timeToLive = timeToLive as Integer
    this.isProduction = isProduction 

    connection = new ConnectionStringBuilder( serviceUrl, serviceName )
    sendClient = new QueueClient( connection, ReceiveMode.PEEKLOCK)

  }

  void queueMessage( Map data, String label  ) throws Exception {

    data.isProduction = this.isProduction
    String randomNumber = "-" + ( ( Math.random() * 1000 ) ) as String 
    Message message = new Message( new JsonBuilder( data ).toPrettyString().getBytes( UTF_8) )
    message.contentType ='application/json'
    message.label = label
    message.messageId = ( new Date().getTime() as String ) + randomNumber 
    message.timeToLive = Duration.ofMinutes( timeToLive ) 
    message.sessionId = getSessionId( data, label )
 
    sendClient.send( message )
    log.info( "Sending message to Admin queue >> Id:${message.getMessageId()}, sessionId:${message.getSessionId()}, type:${message.getLabel()}" )

  }

  String getSessionId( Map data, String adminTopicStr ){

    switch( adminTopicStr ) {
      case AdminTopic.CREATE_CUSTOMER.toString():
             return "CREATE_CUSTOMER_${data.clientId}_${data.isProduction}"
      
      case AdminTopic.CREATE_CREDENTIAL.toString():
             return "CREATE_CREDENTIAL_OR_ACCOUNT${data.customerId}_${data.isProduction}"
      
      case AdminTopic.CREATE_ACCOUNT.toString():
             return "CREATE_CREDENTIAL_OR_ACCOUNT${data.customerId}_${data.isProduction}"
      
      case AdminTopic.CREATE_CONNECTION.toString():
             return "CREATE_CONNECTION_${data.customerId}"
      
      case AdminTopic.CREATE_MOVEMENT.toString():
             return "CREATE_MOVEMENT_${data.accountId}_${data.isProduction}"                  
      default:
        throw new BadImplementationException('adminQueueService.getSessionId.adminTopicStr.notFound')
    }

  }

    @PreDestroy
    public void OnDestroy() {
      sendClient.close()
      log.info('Sender connection to admin queue closed')
    }

}
