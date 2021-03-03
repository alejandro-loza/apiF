package mx.finerio.api.controllers

import mx.finerio.api.services.SummaryService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SummaryController {

  @Autowired
  SummaryService summaryService

  @GetMapping('/summary')
  ResponseEntity findAll( @RequestParam Long customerId ) {
  
    def summaryDto = summaryService.getSummaryByCustomer( customerId )
    new ResponseEntity( summaryDto, HttpStatus.OK )

  }

}
