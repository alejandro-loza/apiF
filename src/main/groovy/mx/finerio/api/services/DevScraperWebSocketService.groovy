package mx.finerio.api.services

import mx.finerio.api.domain.Callback
import mx.finerio.api.dtos.ScraperWebSocketSendDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
@Profile('dev')
class DevScraperWebSocketService implements ScraperWebSocketService {

  @Autowired
  CallbackService callbackService

  @Autowired
  CredentialService credentialService

  @Autowired
  DevScraperService scraperService

  @Override
  @Async
  void send( ScraperWebSocketSendDto scraperWebSocketSendDto )
      throws Exception {

    def credentialId = scraperWebSocketSendDto.id
    def credential = credentialService.findAndValidate( credentialId )

    if ( scraperWebSocketSendDto.tokenSent ) {
      scraperService.requestData( credential )
    } else {

      def dataSend = [ credentialId: credentialId, stage: 'interactive' ]
      Thread.sleep( 3000 )
      callbackService.sendToClient( credential.customer.client,
          Callback.Nature.NOTIFY, dataSend )

    }

  }

  @Override
  void closeSession( String id ) throws Exception {
  }

}
