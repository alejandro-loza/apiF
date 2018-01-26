package mx.finerio.api.controllers

import javax.validation.Valid

import mx.finerio.api.dtos.CustomerDto
import mx.finerio.api.services.CustomerService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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

}
