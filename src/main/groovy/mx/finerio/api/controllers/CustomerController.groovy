package mx.finerio.api.controllers

import javax.validation.Valid

import mx.finerio.api.dtos.CustomerDto
import mx.finerio.api.dtos.CustomerListDto
import mx.finerio.api.services.CustomerService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerController {

  @Autowired
  CustomerService customerService

  @PostMapping('/customers')
  ResponseEntity create( @RequestBody @Valid CustomerDto dto ) {
  
    def instance = customerService.create( dto )
    instance = customerService.getFields( instance )
    new ResponseEntity( instance, HttpStatus.CREATED )

  }

  @GetMapping('/customers')
  ResponseEntity findAll( @RequestParam Map<String, String> params ) {
  
    def response = customerService.findAll( params )
    response.data = response.data.collect {
        customerService.getFields( it ) }
    new ResponseEntity( response, HttpStatus.OK )

  }

  @GetMapping('/customers/{id}')
  ResponseEntity findOne( @PathVariable Long id ) {
  
    def instance = customerService.findOne( id )
    instance = customerService.getFields( instance )
    new ResponseEntity( instance, HttpStatus.OK )

  }

}
