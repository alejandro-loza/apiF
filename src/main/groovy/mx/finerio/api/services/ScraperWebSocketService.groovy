package mx.finerio.api.services

import mx.finerio.api.dtos.ScraperWebSocketSendDto

interface ScraperWebSocketService {

  void send( ScraperWebSocketSendDto scraperWebSocketSendDto )
    throws Exception

  void closeSession( String id ) throws Exception

}
