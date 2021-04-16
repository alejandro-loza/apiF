package mx.finerio.api.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import mx.finerio.api.dtos.SatwsEventDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import mx.finerio.api.services.SatwsService

@RestController
class SatwsController {

  @Autowired
  SatwsService satwsService

  @GetMapping( '/invoices' )
  ResponseEntity getInvoices( @RequestParam Map<String, String> params  ) {
    def response = satwsService.getInvoicesByParams( satwsEventDto )    
    new ResponseEntity( response, HttpStatus.OK )
  }
  
  @GetMapping(path="/invoices/{invoiceId}", produces=["application/json","application/pdf","text/xml"])
  ResponseEntity getInvoice( @PathVariable String invoiceId, @RequestHeader("Accept") String accept ) {    
    def response = satwsService.getInvoice( invoiceId, accept )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @PostMapping( '/FM73Eptuzq24683NNg37GeCHHme4KdaUa2d46S89FCp9MCTnfw' )
  ResponseEntity events( @RequestBody SatwsEventDto satwsEventDto ) {
    satwsService.processEvent( satwsEventDto )    
    ResponseEntity.ok().build()
  }


}
