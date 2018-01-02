package mx.finerio.api.controllers

import mx.finerio.api.dtos.Account
import mx.finerio.api.dtos.Transaction
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

  @PostAccountping( '/api/callbacks/accounts' )
  ResponseEntity accounts(@RequestBody Account accounts) {
    ResponseEntity.ok( [ result: "accounts" ] )
  }

  @PostTransactionping( '/api/callbacks/transactions' )
  ResponseEntity transactions(@RequestBody Transaction transactions) {
    ResponseEntity.ok( [ result: "transactions" ] )
  }

  @PutMapping( '/api/callbacks/success' )
  ResponseEntity success() {
    ResponseEntity.ok( [ result: "success" ] )
  }

  @PutMapping( '/api/callbacks/failure' )
  ResponseEntity failure() {
    ResponseEntity.ok( [ result: "failure" ] )
  }

  @PutMapping( '/api/callbacks/notify' )
  ResponseEntity notif() {
    ResponseEntity.ok( [ result: "notify" ] )
  }

}
