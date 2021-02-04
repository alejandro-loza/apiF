package mx.finerio.api.controllers

import javax.servlet.http.HttpServletRequest

import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Client
import mx.finerio.api.dtos.AccountDto
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.dtos.TransactionDto
import mx.finerio.api.dtos.NotifyCallbackDto
import mx.finerio.api.dtos.SuccessCallbackDto
import mx.finerio.api.dtos.WidgetEventsDto
import mx.finerio.api.dtos.ScraperV2TokenDto
import mx.finerio.api.services.AccountDetailsService
import mx.finerio.api.services.AccountService
import mx.finerio.api.services.AzureQueueService
import mx.finerio.api.services.CallbackService
import mx.finerio.api.services.CredentialService
import mx.finerio.api.services.ScraperCallbackService
import mx.finerio.api.services.WidgetEventsService
import mx.finerio.api.domain.TransactionMessageType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import mx.finerio.api.services.CredentialStateService
import mx.finerio.api.services.ScraperV2TokenService
import com.fasterxml.jackson.databind.ObjectMapper


@RestController
class ScraperCallbackController {

  @Autowired
  AccountDetailsService accountDetailsService

  @Autowired
  AccountService accountService

  @Autowired
  CallbackService callbackService

  @Autowired
  CredentialService credentialService

  @Autowired
  ScraperCallbackService scraperCallbackService
  
  @Autowired
  AzureQueueService azureQueueService

  @Autowired
  WidgetEventsService widgetEventsService

  @Autowired
  CredentialStateService credentialStateService
  
  @Autowired
  ScraperV2TokenService scraperV2TokenService

  @PostMapping( '/callbacks/accounts' )
  ResponseEntity accounts( @RequestBody AccountDto accountDto ) {

    def account = accountService.create( accountDto.data )
    def credential = credentialService.findAndValidate(
        accountDto?.data?.credential_id as String )
    def accountDetails = accountDetailsService.findAllByAccount( account.id )
    
    def data = [
      customerId: credential?.customer?.id,
      credentialId: credential.id,
      accountId: account.id,
      account: accountService.getFields( account ),
      accountDetails: accountDetails
    ]

    credentialStateService.addState( credential.id, data )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.ACCOUNTS, data )
    widgetEventsService.onAccountCreated( new WidgetEventsDto(
        credentialId: credential.id, accountId: account.id,
        accountName: account.name ) )
    ResponseEntity.ok( [ id: account.id ] )

  }

  @PostMapping( '/callbacks/transactions' )
  ResponseEntity transactions( @RequestBody TransactionDto transactionDto ) {
    azureQueueService.queueTransactions( transactionDto, TransactionMessageType.CONTENT )
    ResponseEntity.ok().build()

  }

  @PostMapping( '/callbacks/success' )
  ResponseEntity success(
      @RequestBody SuccessCallbackDto successCallbackDto ) {
     TransactionDto transactionDto = 
      TransactionDto.getInstanceFromCredentialId( successCallbackDto.data?.credential_id )
      azureQueueService.queueTransactions( transactionDto, TransactionMessageType.END )
    ResponseEntity.ok().build()

  }

  @PostMapping( '/callbacks/failure' )
  ResponseEntity failure(
      @RequestBody FailureCallbackDto failureCallbackDto ) {
    scraperCallbackService.processFailure( failureCallbackDto )
    ResponseEntity.ok().build()

  }

  @PostMapping( '/callbacks/notify' )
  ResponseEntity notify( @RequestBody NotifyCallbackDto request ) {
    queueStartNotify(request)
    def credential = credentialService.findAndValidate(
        request?.data?.credential_id as String )
    def data = [
      customerId: credential?.customer?.id,
      credentialId: credential.id,
      stage: request?.data?.stage
    ]
    credentialStateService.addState( credential.id, data )

    if ( data.stage == 'interactive' ) {
      widgetEventsService.onInteractive( new WidgetEventsDto(
          credentialId: credential.id ) )
    }

    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.NOTIFY, data )

  }

  @PostMapping( path = '/kFYfkW3wK65ZeXHQ46kjeF9wrZTKuR5NjUR8G6k37LMs2a9YHM', consumes = 'text/plain')
  ResponseEntity processToken( @RequestBody String scraperTokenDtoString ) {

    def objectMapper = new ObjectMapper()
    def scraperTokenDto = objectMapper.readValue(
          scraperTokenDtoString, ScraperV2TokenDto )
    Credential credential = credentialService.findAndValidate( scraperTokenDto.state )
    Client client = credential.customer.client

    scraperV2TokenService.processOnInteractive( scraperTokenDto, client )
              
    ResponseEntity.ok().build()
  }

  private queueStartNotify( NotifyCallbackDto request ){
    if( request && request?.data?.credential_id 
        && request?.data?.stage?.equals("fetch_transactions") ){

      TransactionDto transactionDto = 
      TransactionDto.getInstanceFromCredentialId( request.data?.credential_id )
      azureQueueService.queueTransactions( transactionDto, TransactionMessageType.START )

    }
  }

}
