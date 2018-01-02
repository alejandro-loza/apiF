package mx.finerio.api.controllers

import mx.finerio.api.services.CredentialService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam


@RestController
class CredentialController {

  @Autowired
  CredentialService credentialService

  @PutMapping( '/credentials/{id}' )
  ResponseEntity updateCredential( @PathVariable String id ) {

    credentialService.requestData( id )
    ResponseEntity.accepted().build()

  }

}
