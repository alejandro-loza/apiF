package mx.finerio.api.controllers

import javax.validation.Valid

import mx.finerio.api.dtos.CredentialDto
import mx.finerio.api.services.CredentialService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CredentialController {

  @Autowired
  CredentialService credentialService

  @PostMapping('/credentials')
  ResponseEntity create( @RequestBody @Valid CredentialDto credentialDto ) {
  
    def instance = credentialService.create( credentialDto )
    instance = credentialService.getFields( instance )
    new ResponseEntity( instance, HttpStatus.CREATED )
  }

  @PutMapping('/credentials/{id}')
  ResponseEntity update( @PathVariable String id ) {

    credentialService.requestData( id )
    ResponseEntity.accepted().build()

  }

  @GetMapping('/credentials')
  ResponseEntity findAll( @RequestParam Map<String, String> params ) {

    def response = credentialService.findAll( params )
    response.data = response.data.collect {
        credentialService.getFields( it ) }
    new ResponseEntity( response, HttpStatus.OK )

  }

}
