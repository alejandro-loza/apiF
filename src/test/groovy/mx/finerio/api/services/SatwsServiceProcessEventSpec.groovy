package mx.finerio.api.services


import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.dtos.SatwsEventDto
import mx.finerio.api.dtos.SatwsEventDataDto
import mx.finerio.api.dtos.SatwsObjectDto
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.Credential
import spock.lang.Specification

class SatwsServiceProcessEventSpec extends Specification {

  def service = new SatwsService()

  def financialInstitutionService = Mock( FinancialInstitutionService )
  def credentialService = Mock( CredentialService )
  def credentialFailureService= Mock( CredentialFailureService )
  
  def setup() {

    service.financialInstitutionService = financialInstitutionService
    service.credentialService = credentialService
    service.credentialFailureService = credentialFailureService
  
  }

 

  def "'dto.type' is null"() {

    when:
      service.processEvent( satwsEventDto )
    then:
      BadImplementationException e = thrown()
      e.message == 'satwsService.processEvent.type.null'
    where:
     satwsEventDto = new SatwsEventDto()

  }


  def "'dto.data.object.id' (credentialId) is null"() {

    when:
      service.processEvent( satwsEventDto )
    then:
      BadImplementationException e = thrown()
      e.message == 'satwsService.processEvent.credentialId.null'
    where:
     satwsEventDto = new SatwsEventDto( type:'anyType' )

  }




  def "process failure sucessfully"() {

    when:
      service.processEvent( satwsEventDto )
    then:
      1 * credentialFailureService.processFailure( _ as FailureCallbackDto ) >> {}
    where:
     satwsEventDto = new SatwsEventDto( 
          type:'credential.updated', data: 
          new SatwsEventDataDto(
            object: new SatwsObjectDto( id: 'someId', status: 'invalid' )))

  }

    def "process sucess sucessfully"() {

    when:
      service.processEvent( satwsEventDto )
    then:      
      1 * credentialService.updateProviderId( _ as String, _ as String )>> new Credential()      
      1 * credentialFailureService.processFailure( _ as FailureCallbackDto ) >> {}
    where:
     satwsEventDto = new SatwsEventDto( 
          type:'link.updated', data: 
          new SatwsEventDataDto(
            object: new SatwsObjectDto( id: 'someId' )))

  }







}
