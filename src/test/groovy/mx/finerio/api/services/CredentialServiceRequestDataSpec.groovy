package mx.finerio.api.services

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceRequestDataSpec extends Specification {

  def service = new CredentialService()

  def credentialPersistenceService = Mock( CredentialPersistenceService )
  def scraperService = Mock( DevScraperService )
  def credentialRepository = Mock( CredentialRepository )

  def setup() {

    service.credentialPersistenceService = credentialPersistenceService
    service.scraperService = scraperService
    service.credentialRepository = credentialRepository

  }

  def "invoking method successfully"() {

    when:
      service.requestData( credentialId )
    then:
      1 * credentialPersistenceService.findOne( _ as String ) >>
          new Credential( user: new User(),
          institution: new FinancialInstitution() )
      1 * credentialRepository.save( _ as Credential )
      1 * scraperService.requestData( _ as Map ) >> [ hello: 'world' ]
    where:
      credentialId = UUID.randomUUID().toString()

  }

  def "parameter 'credentialId' is null"() {

    when:
      service.requestData( credentialId )
    then:
      1 * credentialPersistenceService.findOne( credentialId ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'credential.requestData.credential.null'
    where:
      credentialId = null

  }

  def "parameter 'credentialId' is empty"() {

    when:
      service.requestData( credentialId )
    then:
      1 * credentialPersistenceService.findOne( credentialId ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'credential.requestData.credential.null'
    where:
      credentialId = ''

  }

  def "parameter 'credentialId' is invalid"() {

    when:
      service.requestData( credentialId )
    then:
      1 * credentialPersistenceService.findOne( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'credential.requestData.credential.null'
    where:
      credentialId = UUID.randomUUID().toString()

  }

}
