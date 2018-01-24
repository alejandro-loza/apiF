package mx.finerio.api.controllers

import mx.finerio.api.services.AccountService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class AccountController {

  @Autowired
  AccountService accountService

  @GetMapping( '/accounts/{id}' )
  ResponseEntity getAccount( @PathVariable String id, Pageable pageable ) {

    accountService.findByCredentialId( id, pageable )
    ResponseEntity.accepted().build()

  }


}
