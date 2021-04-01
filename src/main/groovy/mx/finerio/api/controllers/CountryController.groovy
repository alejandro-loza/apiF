package mx.finerio.api.controllers

import mx.finerio.api.services.CountryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CountryController {

  @Autowired
  CountryService countryService

  @GetMapping('/countries')
  ResponseEntity findAll() {
  
    def response = countryService.findAll()
    response = response.data.collect {
      countryService.getFields( it ) }
    new ResponseEntity( response, HttpStatus.OK )

  }

}
