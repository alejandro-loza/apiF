package mx.finerio.api.services


import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.dtos.*
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import spock.lang.Specification

class SatwsServiceProcessEventSpec extends Specification {

  def service = new SatwsService()

  def financialInstitutionService = Mock( FinancialInstitutionService )
  def credentialService = Mock( CredentialService )
  def credentialFailureService= Mock( CredentialFailureService )
  def scraperCallbackService= Mock( ScraperCallbackService )
  def satwsClientService= Mock( SatwsClientService )
  def emailRestService= Mock( EmailRestService )

  def setup() {

    service.financialInstitutionService = financialInstitutionService
    service.credentialService = credentialService
    service.credentialFailureService = credentialFailureService
    service.scraperCallbackService = scraperCallbackService
    service.satwsClientService = satwsClientService
    service.emailRestService = emailRestService
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

    def "process success sucessfully"() {

      when:
        service.processEvent( satwsEventDto )
      then:      
        7 * satwsClientService.createExtraction( _ as CreateExtractionDto )
        1 * credentialService.updateProviderId( _ as String, _ as String )>> new Credential()
        1 * scraperCallbackService.processSuccess( _ as SuccessCallbackDto )>> new Credential()
        1 * scraperCallbackService.postProcessSuccess( _ as Credential )
      where:
       satwsEventDto = new SatwsEventDto( 
            type:'link.created', data: 
            new SatwsEventDataDto(
              object: new SatwsObjectDto( id: 'someId', credential: new SatwsObjectCredentialDto
                ( rfc: 'YUJA8707049U3',status: 'valid',metadata: new SatwObjectMetadataDto(credentialId:'somecredentialId')) )))

  }


   def "process extraction sucessfully"() {

      when:
        service.processEvent( satwsEventDto )
      then:      
       1 * credentialService.findAndValidate( _ as String ) >> new Credential( username:'someusername', customer: new Customer(name:'somename'))
       1 * satwsClientService.getExtractions( _ as Map ) >> ['hydra:member': [ [ status: 'finished'], [status: 'finished'] ] ]
       1 * emailRestService.send( _ as EmailSendDto )
      where:
       satwsEventDto = new SatwsEventDto( 
            type:'extraction.updated', data: 
            new SatwsEventDataDto(
              object: new SatwsObjectDto( id: 'someId', metadata: new SatwObjectMetadataDto(credentialId:'somecredentialId') )))

  }







}
