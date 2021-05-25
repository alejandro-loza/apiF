package mx.finerio.api.services


import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.dtos.CreateCredentialSatwsDto
import spock.lang.Specification
import wslite.rest.RESTClient
import wslite.http.HTTPResponse

class SatwsClientCreateCredentialSpec extends Specification {

  def service = new SatwsClientService()
  

  def "type is null"() {

    when:
      service.createCredential( dto )
    then:
      BadImplementationException e = thrown()
      e.message == 'satwsClientService.validateInputCreateCredential.type.null'
    where:
     dto = new CreateCredentialSatwsDto( rfc : 'BATD8906849L3', password: 'dfsdfsdf')      
  }


  def "rfc is null"() {

    when:
      service.createCredential( dto )
    then:
      BadImplementationException e = thrown()
      e.message == 'satwsClientService.validateInputCreateCredential.rfc.null'
    where:
     dto = new CreateCredentialSatwsDto( type : 'ciec ', password: 'dfsdfsdf')      
  }

  
  def "password is null"() {

    when:
      service.createCredential( dto )
    then:
      BadImplementationException e = thrown()
      e.message == 'satwsClientService.validateInputCreateCredential.password.null'
    where:
     dto = new CreateCredentialSatwsDto( type : 'ciec ', rfc : 'BATD8906849L3')      
  }

  
}
