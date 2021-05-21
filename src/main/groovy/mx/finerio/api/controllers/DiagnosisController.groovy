package mx.finerio.api.controllers

import mx.finerio.api.services.DiagnosisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class DiagnosisController {

  @Autowired
  DiagnosisService diagnosisService

  @GetMapping('/diagnosis')
  ResponseEntity findAll( @RequestParam Long customerId, @RequestParam Optional<BigDecimal>  averageIncome) {
  
    new ResponseEntity( diagnosisService.getDiagnosisByCustomer( customerId, averageIncome ), HttpStatus.OK )

  }

}
