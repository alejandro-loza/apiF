package mx.finerio.api.controllers

import javax.validation.Valid
import mx.finerio.api.dtos.*
import mx.finerio.api.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.*
import org.springframework.web.bind.annotation.*

@RestController
class ClientController {

  @Autowired
  ClientService clientService

  @PostMapping('/client/create')
  ResponseEntity create( @RequestBody @Valid ClientDto dto ) {
  
    def instance = clientService.create( dto )
    new ResponseEntity( instance, HttpStatus.CREATED )

  }

  @GetMapping('/client')
  ResponseEntity findOne() {

    def instance = clientService.findOne()
    new ResponseEntity( instance, HttpStatus.OK )

  }

  @PutMapping('/client')
  ResponseEntity update( @RequestBody @Valid UpdateClientDto dto ) {

    def instance = clientService.update( dto )
    new ResponseEntity( instance, HttpStatus.OK )

  }

  @PutMapping('/client/delete')
  ResponseEntity deleteClient() {

    clientService.deleteClient( )
    ResponseEntity.accepted().build()

  }

}
