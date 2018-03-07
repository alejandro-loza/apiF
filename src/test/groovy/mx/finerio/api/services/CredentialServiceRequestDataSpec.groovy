package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceRequestDataSpec extends Specification {

  def service = new CredentialService()

  def bankConnectionService = Mock( BankConnectionService )
  def scraperService = Mock( DevScraperService )
  def scraperWebSocketService = Mock( ScraperWebSocketService )
  def securityService = Mock( SecurityService )
  def credentialRepository = Mock( CredentialRepository )

  def setup() {

    service.bankConnectionService = bankConnectionService
    service.scraperService = scraperService
    service.scraperWebSocketService = scraperWebSocketService
    service.securityService = securityService
    service.credentialRepository = credentialRepository

  }

  def "invoking method successfully"() {

    when:
      service.requestData( credentialId )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ),
          institution: new FinancialInstitution(),
          user: new User() )
      1 * credentialRepository.save( _ as Credential )
      1 * bankConnectionService.create( _ as Credential )
      1 * scraperService.requestData( _ as Map ) >> [ hello: 'world' ]
    where:
      credentialId = UUID.randomUUID().toString()
      client = new Client( id: 1 )

  }

  def "invoking method successfully (interactive bank)"() {

    when:
      service.requestData( credentialId )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ),
          institution: new FinancialInstitution( code: 'BBVA'),
          user: new User() )
      1 * credentialRepository.save( _ as Credential )
      1 * bankConnectionService.create( _ as Credential )
      1 * scraperWebSocketService.send( _ as String )
    where:
      credentialId = UUID.randomUUID().toString()
      client = new Client( id: 1 )

  }

  def "parameter 'credentialId' is null"() {

    when:
      service.requestData( credentialId )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findOne.id.null'
    where:
      credentialId = null

  }

  def "parameter 'credentialId' is blank"() {

    when:
      service.requestData( credentialId )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findOne.id.null'
    where:
      credentialId = ''

  }

  def "parameter 'credentialId' is invalid"() {

    when:
      service.requestData( credentialId )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'credential.not.found'
    where:
      credentialId = UUID.randomUUID().toString()
      client = new Client( id: 1 )

  }

}
