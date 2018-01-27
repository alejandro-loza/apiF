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
    def finalInstance = [ id: instance.id, name: instance.name ]
    new ResponseEntity( finalInstance, HttpStatus.CREATED )

  }

  @GetMapping('/customers')
  ResponseEntity findAll( @RequestParam Map<String, String> params ) {
  
    def dto = new CustomerListDto(
      maxResults: params.maxResults as Integer,
      cursor: params.cursor as Long
    )
    def response = customerService.findAll( dto )
    response.data = response.data.collect {
        [ id: it.id, name: it.name ] }
    new ResponseEntity( response, HttpStatus.OK )

  }

}
