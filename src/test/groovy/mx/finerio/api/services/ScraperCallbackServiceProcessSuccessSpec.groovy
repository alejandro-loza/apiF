package mx.finerio.api.services

import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.dtos.SuccessCallbackData
import mx.finerio.api.dtos.SuccessCallbackDto
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class ScraperCallbackServiceProcessSuccessSpec extends Specification {

  def service = new ScraperCallbackService()

  def callbackService = Mock( CallbackService )
  def credentialService = Mock( CredentialService )
  def scraperWebSocketService = Mock( ScraperWebSocketService )
  def credentialStatusHistoryService = Mock( CredentialStatusHistoryService )

  def setup() {

    service.callbackService = callbackService
    service.credentialService = credentialService
    service.scraperWebSocketService = scraperWebSocketService
    service.credentialStatusHistoryService = credentialStatusHistoryService

  }

  def "everything was OK"() {

    when:
      service.processSuccess( successCallbackDto )
    then:
      1 * credentialService.updateStatus( _ as String,
          _ as Credential.Status ) >>
          new Credential( id: 'id', customer: new Customer(
          client: new Client() ),
          institution: new FinancialInstitution( code: 'BBVA' ) )
      1 * credentialStatusHistoryService.update( _ as Credential )    
    where:
      successCallbackDto = getSuccessCallbackDto()

  }

  def "parameter 'successCallbackDto' is null"() {

    when:
      service.processSuccess( successCallbackDto )
    then:
      BadImplementationException e = thrown()
      e.message ==
          'scraperCallbackService.processSuccess.successCallbackDto.null'
    where:
      successCallbackDto = null

  }

  private SuccessCallbackDto getSuccessCallbackDto() throws Exception {

    new SuccessCallbackDto(
      data: new SuccessCallbackData( credential_id: 'id' )
    )

  }

}
