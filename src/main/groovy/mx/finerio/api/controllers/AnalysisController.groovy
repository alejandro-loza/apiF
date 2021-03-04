package mx.finerio.api.controllers

import mx.finerio.api.services.AnalysisService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AnalysisController {

  @Autowired
  AnalysisService analysisService

  @GetMapping('/analysis')
  ResponseEntity findAll( @RequestParam Long customerId ) {
  
    def analysisDto = analysisService.getAnalysisByCustomer( customerId )
    new ResponseEntity( analysisDto, HttpStatus.OK )

  }

}
