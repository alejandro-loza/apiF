package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.dtos.CredentialInteractiveDto
import mx.finerio.api.dtos.ScraperWebSocketSendDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceProcessInteractiveSpec extends Specification {

  def service = new CredentialService()

  def scraperV2TokenService = Mock( ScraperV2TokenService )
  def securityService = Mock( SecurityService )
  def credentialRepository = Mock( CredentialRepository )
  def widgetEventsService = Mock( WidgetEventsService )
  def institutionsWithToken = 'BBVA,BNMX'

  def setup() {

    service.scraperV2TokenService = scraperV2TokenService
    service.securityService = securityService
    service.credentialRepository = credentialRepository
    service.widgetEventsService = widgetEventsService
    service.institutionsWithToken = institutionsWithToken

  }

  def "invoking method successfully"() {

    when:
      service.processInteractive( id, credentialInteractiveDto )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ),
          institution: new FinancialInstitution(code: 'BBVA'),
          user: new User() )
      1 * scraperV2TokenService.send(
          _ as String, _ as String, _ as String )
    where:
      id = UUID.randomUUID().toString()
      client = new Client( id: 1 )
      credentialInteractiveDto = new CredentialInteractiveDto(
          token: '123' )

  }

  def "parameter 'id' is null"() {

    when:
      service.processInteractive( id, credentialInteractiveDto )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findOne.id.null'
    where:
      id = null
      credentialInteractiveDto = new CredentialInteractiveDto()

  }

  def "parameter 'id' is blank"() {

    when:
      service.processInteractive( id, credentialInteractiveDto )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findOne.id.null'
    where:
      id = ''
      credentialInteractiveDto = new CredentialInteractiveDto()

  }

  def "parameter 'id' is invalid"() {

    when:
      service.processInteractive( id, credentialInteractiveDto )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'credential.not.found'
    where:
      id = UUID.randomUUID().toString()
      client = new Client( id: 1 )
      credentialInteractiveDto = new CredentialInteractiveDto()

  }

  def "parameter 'credentialInteractiveDto' is null"() {

    when:
      service.processInteractive( id, credentialInteractiveDto )
    then:
      BadRequestException e = thrown()
      e.message == 'credentialService.processInteractive.credentialInteractiveDto.null'
    where:
      id = UUID.randomUUID().toString()
      credentialInteractiveDto = null

  }

}
