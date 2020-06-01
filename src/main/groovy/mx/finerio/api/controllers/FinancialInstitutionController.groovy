package mx.finerio.api.controllers

import mx.finerio.api.services.BankFieldService
import mx.finerio.api.services.FinancialInstitutionService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FinancialInstitutionController {

  @Autowired
  BankFieldService bankFieldService

  @Autowired
  FinancialInstitutionService financialInstitutionService

  @GetMapping('/banks')
  ResponseEntity findAll() {
  
    def response = financialInstitutionService.findAll()
    response = response.data.collect {
        financialInstitutionService.getFields( it ) }
    new ResponseEntity( response, HttpStatus.OK )

  }

  @GetMapping('/banks/{id}/fields')
  ResponseEntity findAll( @PathVariable Long id ) {
  
    def response = bankFieldService.findAllByFinancialInstitution( id )
    response = response.collect { bankFieldService.getFields( it ) }
    response = response.sort { it.position }
    new ResponseEntity( response, HttpStatus.OK )

  }

}
