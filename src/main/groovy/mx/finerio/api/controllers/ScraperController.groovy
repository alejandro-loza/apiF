package mx.finerio.api.controllers

import javax.servlet.http.HttpServletRequest

import mx.finerio.api.dtos.Account
import mx.finerio.api.dtos.Transaction

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

  @PostMapping( '/callbacks/accounts' )
  ResponseEntity accounts( HttpServletRequest request ) {
    log.info( 'request accounts: {}', request.inputStream.text )
    ResponseEntity.ok( [ id: UUID.randomUUID().toString() ] )
  }

  @PostMapping( '/callbacks/transactions' )
  ResponseEntity transactions( HttpServletRequest request ) {
    log.info( 'request txs: {}', request.inputStream.text )
    ResponseEntity.ok( [ id: UUID.randomUUID().toString() ] )
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
