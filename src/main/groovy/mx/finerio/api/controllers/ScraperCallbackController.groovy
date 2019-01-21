package mx.finerio.api.controllers

import javax.servlet.http.HttpServletRequest

import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.Credential
import mx.finerio.api.dtos.AccountDto
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.dtos.TransactionDto
import mx.finerio.api.dtos.NotifyCallbackDto
import mx.finerio.api.dtos.SuccessCallbackDto
import mx.finerio.api.services.AccountService
import mx.finerio.api.services.CallbackService
import mx.finerio.api.services.CredentialService
import mx.finerio.api.services.ScraperCallbackService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@RestController
class ScraperCallbackController {

  @Autowired
  AccountService accountService

  @Autowired
  CallbackService callbackService

  @Autowired
  CredentialService credentialService

  @Autowired
  ScraperCallbackService scraperCallbackService

  @PostMapping( '/callbacks/accounts' )
  ResponseEntity accounts( @RequestBody AccountDto accountDto ) {

    def account = accountService.create( accountDto.data )
    def credential = credentialService.findAndValidate(
        accountDto?.data?.credential_id as String )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.ACCOUNTS, [ credentialId: credential.id,
        accountId: account.id ] )
    ResponseEntity.ok( [ id: account.id ] )

  }

  @PostMapping( '/callbacks/transactions' )
  ResponseEntity transactions( @RequestBody TransactionDto transactionDto ) {

    scraperCallbackService.queueTransactions( transactionDto )
    ResponseEntity.ok().build()

  }

  @PostMapping( '/callbacks/success' )
  ResponseEntity success(
      @RequestBody SuccessCallbackDto successCallbackDto ) {

    scraperCallbackService.processSuccess( successCallbackDto )
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

    def credential = credentialService.findAndValidate(
        request?.data?.credential_id as String )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.NOTIFY, [ credentialId: credential.id,
        stage: request?.data?.stage  ] )

  }

}
