package mx.finerio.api.services

import mx.finerio.api.domain.Callback
import mx.finerio.api.dtos.WidgetEventsDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
@Profile('dev')
class DevScraperService extends ScraperApiService {

  @Autowired
  CallbackService callbackService

  @Autowired
  CredentialService credentialService

  @Autowired
  WidgetEventsService widgetEventsService

  @Override
  @Async
  Map requestData( Map data ) throws Exception {

    if ( data.institution.id == 12 ) {

      def credentialId = data.id
      def credential = credentialService.findAndValidate( credentialId )
      def bankToken = getBankToken()
      def dataSend = [ credentialId: credentialId, stage: 'interactive',
          bankToken: bankToken ]
      Thread.sleep( 3000 )
      widgetEventsService.onInteractive( new WidgetEventsDto(
          credentialId: credentialId, bankToken: bankToken ) )
      callbackService.sendToClient( credential.customer.client,
          Callback.Nature.NOTIFY, dataSend )

    } else {
      return super.requestData( data )
    }

  }

  private String getBankToken() throws Exception {
    return '12345678'
  }

}
