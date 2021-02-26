package mx.finerio.api.services

import org.springframework.stereotype.Service
import mx.finerio.api.domain.Callback
import mx.finerio.api.dtos.CreateCredentialDto
import mx.finerio.api.dtos.WidgetEventsDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async

@Service
@Profile('dev')
class DevScraperV2Service implements ScraperV2Service {

  @Autowired
  CallbackService callbackService

  @Autowired
  CredentialService credentialService

  @Autowired
  ScraperService scraperService
   
  @Autowired
  WidgetEventsService widgetEventsService

  @Override
  @Async
  void createCredential( CreateCredentialDto createCredentialDto ) throws Exception {
  
    def credential = credentialService.findAndValidate(
        createCredentialDto.credentialId )
    def institutionId = credential.institution.id

    if ( institutionId == 1 ) {

      def credentialId = credential.id
      def bankToken = null
      def dataSend = [ credentialId: credentialId, stage: 'interactive',
          bankToken: bankToken ]
      Thread.sleep( 3000 )
      widgetEventsService.onInteractive( new WidgetEventsDto(
          credentialId: credentialId, bankToken: bankToken ) )
      callbackService.sendToClient( credential.customer.client,
          Callback.Nature.NOTIFY, dataSend )

    } else {

      def data = [
        id: credential.id,
        username: credential.username,
        password: credential.password,
        iv: credential.iv,
        user: [ id: credential.user.id ],
        institution: [ id: credential.institution.id ],
        securityCode: credential.securityCode
      ]

      scraperService.requestData( data )

    }

  }

  @Override
  void createCredentialLegacyPayload( Map data ) throws Exception { 
    scraperService.requestData( data )                    
  }




}
