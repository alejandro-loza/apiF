package mx.finerio.api.controllers

import javax.servlet.http.HttpServletRequest

import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.Credential
import mx.finerio.api.dtos.AccountBody
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.dtos.MovementBody
import mx.finerio.api.dtos.NotifyCallbackDto
import mx.finerio.api.dtos.SuccessCallbackDto
import mx.finerio.api.services.AccountService
import mx.finerio.api.services.CallbackService
import mx.finerio.api.services.CredentialService
import mx.finerio.api.services.MovementService

import org.slf4j.Logger
import org.slf4j.LoggerFactory

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

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.controllers.ScraperController' )

  @Autowired
  AccountService accountService

  @Autowired
  CallbackService callbackService

  @Autowired
  CredentialService credentialService

  @Autowired
  MovementService movementService

  @PostMapping( '/callbacks/accounts' )
  ResponseEntity accounts( @RequestBody AccountBody request ) {

    Map map = [ 'request': request.data ]
    def account = accountService.createAccount( map )
    ResponseEntity.ok( [ id: account.id ] )

  }

  @PostMapping( '/callbacks/transactions' )
  ResponseEntity transactions( @RequestBody MovementBody request ) {

    Map map = [ 'request': request.data ]
    def movement = movementService.createMovement( map )
    ResponseEntity.ok().build()

  }

  @PostMapping( '/callbacks/success' )
  ResponseEntity success( @RequestBody SuccessCallbackDto request ) {

    def credential = credentialService.updateStatus(
        request?.data?.credential_id, Credential.Status.ACTIVE )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.SUCCESS, [ credentialId: credential.id ] )

  }

  @PostMapping( '/callbacks/failure' )
  ResponseEntity failure( @RequestBody FailureCallbackDto request ) {

    def credential = credentialService.setFailure(
        request?.data?.credential_id, request?.data?.error_message )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.FAILURE, [ credentialId: credential.id,
        message: credential.errorCode  ] )

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
