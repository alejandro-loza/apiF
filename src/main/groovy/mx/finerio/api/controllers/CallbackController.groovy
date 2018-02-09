package mx.finerio.api.controllers

import javax.validation.Valid

import mx.finerio.api.dtos.CallbackDto
import mx.finerio.api.services.CallbackService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CallbackController {

  @Autowired
  CallbackService callbackService

  @PostMapping('/clients/callbacks')
  ResponseEntity create( @RequestBody @Valid CallbackDto callbackDto ) {
  
    def instance = callbackService.create( callbackDto )
    instance = callbackService.getFields( instance )
    new ResponseEntity( instance, HttpStatus.CREATED )

  }

}
