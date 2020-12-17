package mx.finerio.api.services

import javax.websocket.ClientEndpoint
import javax.websocket.ContainerProvider
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session

import mx.finerio.api.dtos.FailureCallbackData
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.dtos.ScraperWebSocketSendDto
import mx.finerio.api.exceptions.BadImplementationException

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@Profile('prod')
class ProdScraperWebSocketService implements ScraperWebSocketService {

  @Value('${scraper.ws.path}')
  String url

  @Value('${scraper.ws.timeout.message}')
  String timeoutMessage

  @Autowired
  CredentialFailureService credentialFailureService

  def sessions = [:]
  
  @Override
  void send( ScraperWebSocketSendDto scraperWebSocketSendDto )
      throws Exception {

    if ( !scraperWebSocketSendDto ) {
      throw new BadImplementationException(
          'scraperWebSocketService.send.scraperWebSocketSendDto.null' )
    }

    if ( scraperWebSocketSendDto.destroyPreviousSession ) {

      closeSession( scraperWebSocketSendDto.id )
      sessions[ scraperWebSocketSendDto.id ] = [ session: createSession(),
          time: new Date().time ]

    }

    if ( !sessions[ scraperWebSocketSendDto.id ] ) {
      processSessionExpired( scraperWebSocketSendDto.id )
      return
    }

    sessions[ scraperWebSocketSendDto.id ]?.session?.basicRemote?.sendText(
        scraperWebSocketSendDto.message )

    if ( scraperWebSocketSendDto.tokenSent ) {
      closeSession( scraperWebSocketSendDto.id )
    }

  }
  
  @Override
  void closeSession( String id ) throws Exception {

    if ( !id ) {
      throw new BadImplementationException(
          'scraperWebSocketService.closeSession.id.null' )
    }

    sessions.remove( id )?.session?.close()

  }

  @Scheduled(cron = '0 * * * * *')
  void destroySessions() throws Exception {

    def nowMillis = new Date().time

    sessions.findResults {

      if ( nowMillis - it.value.time >= 300000 ) {

        try {
          it.value.session.basicRemote
              .sendText( '{"data":{"stage":"logout"}}' )
        } catch ( Exception e ) {}

        return it

      }

      null

    }.each {

      try {
        processSessionExpired( it.key )
        closeSession( it.key )
      } catch ( Exception e ) {}

    }

  }

  private Session createSession() throws Exception {

    def container = ContainerProvider.webSocketContainer
    container.connectToServer( ScraperClientEndpointService,
        URI.create( url ) )

  }

  private void processSessionExpired( String credentialId )
      throws Exception {

    def dto = new FailureCallbackDto()
    def data = new FailureCallbackData()
    data.credential_id = credentialId
    data.error_message = timeoutMessage
    data.status_code = 401
    dto.data = data
    credentialFailureService.processFailure( dto )

  }

}

@ClientEndpoint
class ScraperClientEndpointService {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.services.ScraperClientEndpointService' )

  @OnOpen
  void onOpen( Session session ) {
    log.info( "<< session opened: {}", session.id )
  }

  @OnMessage
  void processMessage( byte[] message ) {
    log.info( "<< message: {}", new String( message, 'UTF-8' ) )
  }

  @OnError
  void processError( Throwable t ) {
    log.info( "XX ${t.class.simpleName} - ${t.message}" )
  }

}
