package mx.finerio.api.controllers

import mx.finerio.api.dtos.*
import mx.finerio.api.services.CredentialService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.*
import org.springframework.web.bind.annotation.*


@RestController
class CredentialController {

  @Autowired
  CredentialService credentialService

  @PutMapping( '/credentials/{id}' )
  ResponseEntity updateCredential( @PathVariable String id ) {

    credentialService.requestData( id )
    ResponseEntity.accepted().build()

  }

  @PostMapping(path="/create")
  ResponseEntity createCredential (@RequestBody CredentialDto credential) {
  
    credentialService.createCredential( credential )
    new ResponseEntity(HttpStatus.CREATED)
  }

}
