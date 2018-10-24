package mx.finerio.api.services

import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.dtos.FailureCallbackData
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class ScraperCallbackServiceProcessFailureSpec extends Specification {

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
      service.processFailure( failureCallbackDto )
    then:
      1 * credentialService.setFailure( _ as String, _ as String ) >>
          new Credential( id: 'id', customer: new Customer(
          client: new Client() ),
          institution: new FinancialInstitution( code: 'BBVA' ) )
      1 * credentialStatusHistoryService.update( _ as Credential )    
      1 * callbackService.sendToClient( _ as Client, _ as Callback.Nature,
          _ as Map )
      1 * scraperWebSocketService.closeSession( _ as String )
    where:
      failureCallbackDto = getFailureCallbackDto()

  }

  def "parameter 'failureCallbackDto' is null"() {

    when:
      service.processFailure( failureCallbackDto )
    then:
      BadImplementationException e = thrown()
      e.message ==
          'scraperCallbackService.processFailure.failureCallbackDto.null'
    where:
      failureCallbackDto = null

  }

  private FailureCallbackDto getFailureCallbackDto() throws Exception {

    new FailureCallbackDto(
      data: new FailureCallbackData( credential_id: 'id',
          error_message: 'error_message' )
    )

  }

}
