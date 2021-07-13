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
      service.getInvoicesByParams( rfc, params,customerId )
    then:
      BadImplementationException e = thrown()
      e.message == 'satwsClientService.getInvoicesByParams.rfc.null'
    where:
     customerId=1523
     rfc = null
     params = [:]
  }
  
}
