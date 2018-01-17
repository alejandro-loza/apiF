package mx.finerio.api.controllers

import javax.servlet.http.HttpServletRequest

import mx.finerio.api.dtos.AccountBody
import mx.finerio.api.dtos.MovementBody
import mx.finerio.api.services.AccountService
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
class ScraperController {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.controllers.ScraperController' )

  @Autowired
  AccountService accountService

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
  ResponseEntity success( HttpServletRequest request ) {
    log.info( 'request success: {}', request.inputStream.text )
  }

  @PostMapping( '/callbacks/failure' )
  ResponseEntity failure( HttpServletRequest request ) {
    log.info( 'request failure: {}', request.inputStream.text )
  }

  @PostMapping( '/callbacks/notify' )
  ResponseEntity notify( HttpServletRequest request ) {
    log.info( 'request notify: {}', request.inputStream.text )
  }

}
