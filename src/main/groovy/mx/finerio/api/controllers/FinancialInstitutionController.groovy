package mx.finerio.api.controllers

import mx.finerio.api.services.FinancialInstitutionService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FinancialInstitutionController {

  @Autowired
  FinancialInstitutionService financialInstitutionService

  @GetMapping('/banks')
  ResponseEntity findAll() {
  
    def response = financialInstitutionService.findAll()
    response = response.data.collect {
        financialInstitutionService.getFields( it ) }

    new ResponseEntity( response, HttpStatus.OK )

  }

}
