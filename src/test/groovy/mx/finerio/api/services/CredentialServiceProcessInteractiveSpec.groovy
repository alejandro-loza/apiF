package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.dtos.CredentialInteractiveDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceProcessInteractiveSpec extends Specification {

  def service = new CredentialService()

  def scraperWebSocketService = Mock( ScraperWebSocketService )
  def securityService = Mock( SecurityService )
  def credentialRepository = Mock( CredentialRepository )

  def setup() {

    service.scraperWebSocketService = scraperWebSocketService
    service.securityService = securityService
    service.credentialRepository = credentialRepository

  }

  def "invoking method successfully"() {

    when:
      service.processInteractive( id, credentialInteractiveDto )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ),
          institution: new FinancialInstitution(),
          user: new User() )
      1 * scraperWebSocketService.send( _ as String )
    where:
      id = UUID.randomUUID().toString()
      client = new Client( id: 1 )
      credentialInteractiveDto = new CredentialInteractiveDto()

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
      BadImplementationException e = thrown()
      e.message == 'credentialService.processInteractive.credentialInteractiveDto.null'
    where:
      id = UUID.randomUUID().toString()
      credentialInteractiveDto = null

  }

}
