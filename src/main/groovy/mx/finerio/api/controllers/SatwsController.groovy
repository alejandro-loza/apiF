package mx.finerio.api.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import mx.finerio.api.dtos.SatwsEventDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import mx.finerio.api.services.SatwsService
import org.springframework.http.HttpStatus

@RestController
class SatwsController {

  @Autowired
  SatwsService satwsService

  @GetMapping( '/invoices' )
  ResponseEntity getInvoices( @RequestParam Map<String, String> params  ) {
    def response = satwsService.getInvoicesByParams( params )    
    new ResponseEntity( response, HttpStatus.OK )
  }
  
  @GetMapping(path="/invoices/{invoiceId}", produces=["application/json","application/pdf","text/xml"])
  ResponseEntity getInvoice( @PathVariable String invoiceId, @RequestHeader("Accept") String accept ) {    
    def response = satwsService.getInvoice( invoiceId, accept )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping( '/links' )
  ResponseEntity getLinksByParams( @RequestParam Map<String, String> params  ) {
    def response = satwsService.getLinksByParams( params )    
    new ResponseEntity( response, HttpStatus.OK )
  }

  @GetMapping(path="/links/{linkId}")
  ResponseEntity getLink( @PathVariable String linkId ) {    
    def response = satwsService.getLink( linkId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @DeleteMapping(path="/links/{linkId}")
  ResponseEntity deleteLink( @PathVariable String linkId ) {    
    def response = satwsService.deleteLink( linkId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping( '/invoices/payments' )
  ResponseEntity getPayments( @RequestParam Map<String, String> params  ) {
    def response = satwsService.getPayments( params )    
    new ResponseEntity( response, HttpStatus.OK )
  }

  @GetMapping(path="/invoices/payments/{paymentId}")
  ResponseEntity getPayment( @PathVariable String paymentId ) {    
    def response = satwsService.getLink( paymentId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping(path="/invoices/{invoiceId}/payments")
  ResponseEntity getInvoicePayments( @PathVariable String invoiceId, @RequestParam Map<String, String> params ) {    
    def response = satwsService.getInvoicePayments( invoiceId, params )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping(path="/customers/{customerId}/invoices/payments")
  ResponseEntity getTaxpayerInvoicePayments( @PathVariable Long customerId, @RequestParam Map<String, String> params ) {
    def response = satwsService.getTaxpayerInvoicePayments( customerId, params )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @PostMapping( '/FM73Eptuzq24683NNg37GeCHHme4KdaUa2d46S89FCp9MCTnfw' )
  ResponseEntity events( @RequestBody SatwsEventDto satwsEventDto ) {
    satwsService.processEvent( satwsEventDto )    
    ResponseEntity.ok().build()
  }


}
