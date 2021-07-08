package mx.finerio.api.services


import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.dtos.SatwsEventDto
import mx.finerio.api.dtos.SatwsEventDataDto
import mx.finerio.api.dtos.SatwsObjectDto
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import spock.lang.Specification

class SatwsServiceGetInvoiceSpec extends Specification {

  def service = new SatwsService()

  def satwsClientService= Mock( SatwsClientService )
  

  def setup() {

    service.satwsClientService = satwsClientService
  
  }

  def "invoiceId is null"() {

    when:
      service.getInvoice( invoiceId, accept )
    then:
      BadImplementationException e = thrown()
      e.message == 'satwsService.getInvoice.invoiceId.null'
    where:
     invoiceId = null
     accept = 'application/json'

  }


   def "accept is null"() {

    when:
      service.getInvoice( invoiceId, accept )
    then:
      BadImplementationException e = thrown()
      e.message == 'satwsService.getInvoice.accept.null'
    where:
     invoiceId = 'dsfsd-fdfer4-445-322'
     accept = null

  }

  def "getInvoice sucessfully"() {

    when:
      def result = service.getInvoice( invoiceId, accept )
    then:
      1 * satwsClientService.getInvoice( _ as String, _ as String ) >> 'result'
      result instanceof String
    where:
     invoiceId = 'dsfsd-fdfer4-445-322'
     accept = 'application/json'

  }
  
}
