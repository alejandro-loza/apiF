package mx.finerio.api.controllers

import mx.finerio.api.services.MovementService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class MovementController {

  @Autowired
  MovementService movementService

  @GetMapping('/transactions')
  ResponseEntity findAll( @RequestParam Map<String, String> params ) {

    def response = movementService.findAll( params )
    response.data = response.data.collect {
        movementService.getFields( it ) }
    new ResponseEntity( response, HttpStatus.OK )

  }

}
