package mx.finerio.api.services

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceUpdateStatusSpec extends Specification {

  def service = new CredentialService()

  def credentialPersistenceService = Mock( CredentialPersistenceService )
  def credentialRepository = Mock( CredentialRepository )

  def setup() {

    service.credentialPersistenceService = credentialPersistenceService
    service.credentialRepository = credentialRepository

  }

  def "invoking method successfully"() {

    when:
      service.updateStatus( credentialId, status )
    then:
      1 * credentialPersistenceService.findOne( _ as String ) >>
          new Credential( user: new User(),
          institution: new FinancialInstitution() )
      1 * credentialRepository.save( _ as Credential )
    where:
      credentialId = UUID.randomUUID().toString()
      status = Credential.Status.ACTIVE

  }

  def "parameter 'credentialId' is null"() {

    when:
      service.updateStatus( credentialId, status )
    then:
      1 * credentialPersistenceService.findOne( credentialId ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'credential.updateStatus.credential.null'
    where:
      credentialId = null
      status = Credential.Status.ACTIVE

  }

  def "parameter 'credentialId' is empty"() {

    when:
      service.updateStatus( credentialId, status )
    then:
      1 * credentialPersistenceService.findOne( credentialId ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'credential.updateStatus.credential.null'
    where:
      credentialId = ''
      status = Credential.Status.ACTIVE

  }

  def "parameter 'credentialId' is invalid"() {

    when:
      service.updateStatus( credentialId, status )
    then:
      1 * credentialPersistenceService.findOne( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'credential.updateStatus.credential.null'
    where:
      credentialId = UUID.randomUUID().toString()
      status = Credential.Status.ACTIVE

  }

  def "parameter 'status' is null"() {

    when:
      service.updateStatus( credentialId, status )
    then:
      1 * credentialPersistenceService.findOne( credentialId ) >>
          new Credential( user: new User(),
          institution: new FinancialInstitution() )
      BadImplementationException e = thrown()
      e.message == 'credentialService.updateStatus.status.null'
    where:
      credentialId = UUID.randomUUID().toString()
      status = null

  }

}
