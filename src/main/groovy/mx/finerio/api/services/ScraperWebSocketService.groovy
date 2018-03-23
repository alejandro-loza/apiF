package mx.finerio.api.services

import javax.websocket.ClientEndpoint
import javax.websocket.ContainerProvider
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session

import mx.finerio.api.dtos.ScraperWebSocketSendDto
import mx.finerio.api.exceptions.BadImplementationException

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ScraperWebSocketService {

  @Value('${scraper.ws.path}')
  String url

  def sessions = [:]
  
  void send( ScraperWebSocketSendDto scraperWebSocketSendDto )
      throws Exception {

    if ( !scraperWebSocketSendDto ) {
      throw new BadImplementationException(
          'scraperWebSocketService.send.scraperWebSocketSendDto.null' )
    }

    closeSession( scraperWebSocketDto.id )
    def session = createSession()
    session.basicRemote.sendText( message )

  }
  
  void closeSession( String id ) throws Exception {

    if ( !id ) {
      throw new BadImplementationException(
          'scraperWebSocketService.closeSession.id.null' )
    }

    sessions.remove( id )?.close()

  }

  private Session createSession() throws Exception {

    def container = ContainerProvider.webSocketContainer
    container.connectToServer( ScraperClientEndpointService,
        URI.create( url ) )

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
