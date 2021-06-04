package mx.finerio.api.services


import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.dtos.CreateCredentialSatwsDto
import spock.lang.Specification
import wslite.rest.RESTClient
import wslite.http.HTTPResponse

class SatwsClientGetInvoicesByParamsSpec extends Specification {

  def service = new SatwsClientService()
  
  
  def "rfc is null"() {

    when:
      service.getInvoicesByParams( rfc, params )
    then:
      BadImplementationException e = thrown()
      e.message == 'satwsClientService.getInvoicesByParams.rfc.null'
    where:
     rfc = null
     params = [:]
  }
  
}
