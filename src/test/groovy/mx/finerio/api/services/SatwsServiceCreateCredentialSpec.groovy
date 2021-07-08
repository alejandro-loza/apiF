package mx.finerio.api.services

import mx.finerio.api.dtos.CreateCredentialSatwsDto
import spock.lang.Specification

class SatwsServiceCreateCredentialSpec extends Specification {

  def service = new SatwsService()

  def satwsClientService= Mock( SatwsClientService )
  
  def setup() {
    service.satwsClientService = satwsClientService  
  }

  def "createCredential sucessfully"() {

    when:
      def result = service.createCredential( dto )
    then:
      1 * satwsClientService.createCredential( _ as CreateCredentialSatwsDto ) >> 'result'
      result instanceof String
    where:
      dto = new CreateCredentialSatwsDto()

  }
  
}
