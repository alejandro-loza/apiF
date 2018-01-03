package mx.finerio.api.services

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialPersistenceServiceFindOneSpec extends Specification {

  def service = new CredentialPersistenceService()

  def credentialRepository = Mock( CredentialRepository )

  def setup() {
    service.credentialRepository = credentialRepository
  }

  def "invoking method successfully"() {

    when:
      def result = service.findOne( id )
    then:
      result instanceof Credential
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( user: new User(),
          institution: new FinancialInstitution() )
    where:
      id = UUID.randomUUID().toString()

  }

  def "parameter 'id' is null"() {

    when:
      def result = service.findOne( id )
    then:
      result == null
    where:
      id = null

  }

  def "parameter 'id' is empty"() {

    when:
      def result = service.findOne( id )
    then:
      result == null
    where:
      id = ''

  }

  def "parameter 'id' is invalid"() {

    when:
      def result = service.findOne( id )
    then:
      result == null
      1 * credentialRepository.findOne( _ as String ) >> null
    where:
      id = UUID.randomUUID().toString()

  }

}
