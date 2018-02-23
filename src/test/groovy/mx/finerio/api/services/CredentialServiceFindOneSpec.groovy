package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.dtos.CredentialDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceFindOneSpec extends Specification {

  def service = new CredentialService()

  def securityService = Mock( SecurityService )
  def credentialRepository = Mock( CredentialRepository )

  def setup() {

    service.securityService = securityService
    service.credentialRepository = credentialRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findOne( id )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ) )
      result instanceof Credential
    where:
      id = 'uuid'
      client = new Client( id: 1 )

  }

  def "parameter 'id' is null"() {

    when:
      service.findOne( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findOne.id.null'
    where:
      id = null

  }

  def "instance not found"() {

    when:
      service.findOne( id )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'credential.not.found'
    where:
      id = 'uuid'
      client = new Client( id: 1 )

  }

  def "instance not found (different client)"() {

    when:
      service.findOne( id )
    then:
      1 * securityService.getCurrent() >> new Client( id: 2 )
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: new Client( id: 1 ) ) )
      InstanceNotFoundException e = thrown()
      e.message == 'credential.not.found'
    where:
      id = 'uuid'

  }

  def "instance not found (dateDeleted is not null)"() {

    when:
      service.findOne( id )
    then:
      1 * securityService.getCurrent() >> new Client( id: 1 )
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: new Client( id: 1 ) ), dateDeleted: new Date() )
      InstanceNotFoundException e = thrown()
      e.message == 'credential.not.found'
    where:
      id = 'uuid'

  }

}
