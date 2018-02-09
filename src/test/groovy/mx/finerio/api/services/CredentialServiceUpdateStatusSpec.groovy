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

class CredentialServiceUpdateStatusSpec extends Specification {

  def service = new CredentialService()

  def securityService = Mock( SecurityService )
  def credentialRepository = Mock( CredentialRepository )

  def setup() {

    service.securityService = securityService
    service.credentialRepository = credentialRepository

  }

  def "invoking method successfully"() {

    when:
      service.updateStatus( credentialId, status )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ),
          institution: new FinancialInstitution(),
          user: new User() )
      1 * credentialRepository.save( _ as Credential )
    where:
      credentialId = UUID.randomUUID().toString()
      status = Credential.Status.ACTIVE
      client = new Client( id: 1 )

  }

  def "parameter 'credentialId' is null"() {

    when:
      service.updateStatus( credentialId, status )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findOne.id.null'
    where:
      credentialId = null
      status = Credential.Status.ACTIVE

  }

  def "parameter 'credentialId' is empty"() {

    when:
      service.updateStatus( credentialId, status )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findOne.id.null'
    where:
      credentialId = ''
      status = Credential.Status.ACTIVE

  }

  def "parameter 'credentialId' is invalid"() {

    when:
      service.updateStatus( credentialId, status )
    then:
      InstanceNotFoundException e = thrown()
      e.message == 'credential.not.found'
    where:
      credentialId = UUID.randomUUID().toString()
      status = Credential.Status.ACTIVE

  }

  def "parameter 'status' is null"() {

    when:
      service.updateStatus( credentialId, status )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.updateStatus.status.null'
    where:
      credentialId = UUID.randomUUID().toString()
      status = null

  }

}
