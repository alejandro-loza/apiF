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

  @PostMapping( '/FM73Eptuzq24683NNg37GeCHHme4KdaUa2d46S89FCp9MCTnfw' )
  ResponseEntity events( @RequestBody SatwsEventDto satwsEventDto ) {
    satwsService.processEvent( satwsEventDto )    
    ResponseEntity.ok().build()
  }

  @GetMapping( '/invoices' )
  ResponseEntity getInvoices( @RequestParam Map<String, String> params  ) {
    def response = satwsService.getInvoicesByParams( params )    
    new ResponseEntity( response, HttpStatus.OK )
  }
  
  @GetMapping(path="/invoices/{invoiceId}", produces=["application/json","application/pdf","text/xml"])
  ResponseEntity getInvoice( @PathVariable String invoiceId,
                             @RequestHeader("Accept") String accept,
                             @RequestParam Map<String, String> params ) {
    def response = satwsService.getInvoice( invoiceId, accept, params )
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
    def response = satwsService.getPayment( paymentId )
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


  @GetMapping( '/invoices/batch-payments' )
  ResponseEntity getBatchPayments( @RequestParam Map<String, String> params  ) {
    def response = satwsService.getBatchPayments( params )    
    new ResponseEntity( response, HttpStatus.OK )
  }

  @GetMapping(path="/invoices/batch-payments/{batchPaymentId}")
  ResponseEntity getBatchPayment( @PathVariable String batchPaymentId ) {    
    def response = satwsService.getBatchPayment( batchPaymentId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping(path="/invoices/{invoiceId}/batch-payments")
  ResponseEntity getInvoiceBatchPayments( @PathVariable String invoiceId, @RequestParam Map<String, String> params ) {    
    def response = satwsService.getInvoiceBatchPayments( invoiceId, params )
    new ResponseEntity( response, HttpStatus.OK )    
  }
  
  @GetMapping(path="/customers/{customerId}/tax-returns")
  ResponseEntity getTaxpayersTaxReturns( @PathVariable Long customerId, @RequestParam Map<String, String> params ) {
    def response = satwsService.getTaxpayersTaxReturns( customerId, params )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping(path="/tax-returns/{taxReturnId}")
  ResponseEntity getTaxReturn( @PathVariable String taxReturnId ) {    
    def response = satwsService.getTaxReturn( taxReturnId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping(path="/tax-returns/{taxReturnId}/data")
  ResponseEntity getTaxReturnData( @PathVariable String taxReturnId ) {    
    def response = satwsService.getTaxReturnData( taxReturnId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @DeleteMapping(path="/tax-returns/{taxReturnId}")
  ResponseEntity deleteTaxReturn( @PathVariable String taxReturnId ) {    
    def response = satwsService.deleteTaxReturn( taxReturnId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping(path="/customers/{customerId}/tax-compliance-checks")
  ResponseEntity getTaxpayersTaxComplianceChecks( @PathVariable Long customerId, @RequestParam Map<String, String> params ) {
    def response = satwsService.getTaxpayersTaxComplianceChecks( customerId, params )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping(path="/tax-compliance-checks/{taxComplianceCheckId}")
  ResponseEntity getTaxComplianceCheck( @PathVariable String taxComplianceCheckId ) {    
    def response = satwsService.getTaxComplianceCheck( taxComplianceCheckId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @DeleteMapping(path="/tax-compliance-checks/{taxComplianceCheckId}")
  ResponseEntity deleteTaxComplianceCheck( @PathVariable String taxComplianceCheckId ) {    
    def response = satwsService.deleteTaxComplianceCheck( taxComplianceCheckId )
    new ResponseEntity( response, HttpStatus.OK )    
  }
  
  @GetMapping( '/extractions' )
  ResponseEntity getExtractions( @RequestParam Map<String, String> params  ) {
    def response = satwsService.getExtractions( params )    
    new ResponseEntity( response, HttpStatus.OK )
  }
  
  @GetMapping( '/extractions/{extractionId}' )
  ResponseEntity getExtraction( @PathVariable String extractionId ) {    
    def response = satwsService.getExtraction( extractionId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping(path="/customers/{customerId}/tax-status")
  ResponseEntity getTaxpayersTaxStatus( @PathVariable Long customerId) {
    def response = satwsService.getTaxpayersTaxStatus( customerId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping( '/tax-status/{taxStatusId}' )
  ResponseEntity getTaxStatus( @PathVariable String taxStatusId ) {    
    def response = satwsService.getTaxStatus( taxStatusId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @DeleteMapping( '/tax-status/{taxStatusId}' )
  ResponseEntity deleteTaxStatus( @PathVariable String taxStatusId ) {    
    def response = satwsService.deleteTaxStatus( taxStatusId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping(path="/customers/{customerId}/tax-retentions")
  ResponseEntity getTaxpayersTaxRetentions( @PathVariable Long customerId, @RequestParam Map<String, String> params ) {
    def response = satwsService.getTaxpayersTaxRetentions( customerId, params )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping( '/tax-retentions/{taxRetentionId}' )
  ResponseEntity getTaxRetention( @PathVariable String taxRetentionId ) {    
    def response = satwsService.getTaxRetention( taxRetentionId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @DeleteMapping( '/tax-retentions/{taxRetentionId}' )
  ResponseEntity deleteTaxRetention( @PathVariable String taxRetentionId ) {    
    def response = satwsService.deleteTaxRetention( taxRetentionId )
    new ResponseEntity( response, HttpStatus.OK )    
  }

  @GetMapping(path="/tax-retentions/{invoiceId}/cfdi", produces=["application/json","application/pdf","text/xml"])
  ResponseEntity getTaxRetentionInvoice( @PathVariable String invoiceId, @RequestHeader("Accept") String accept ) {    
    def response = satwsService.getTaxRetentionInvoice( invoiceId, accept )
    new ResponseEntity( response, HttpStatus.OK )    
  }
  
}
