package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceValidateUserCredentialSpec extends Specification {

  def service = new CredentialService()

  def userService = Mock( UserService )

  def setup() {

    service.userService = userService

  }

  def "invoking method successfully"() {

    when:
      def result = service.validateUserCredential( credential, userId )
    then:
      1 * userService.findById( _ as String ) >> new User( id:"id" )
      result instanceof Credential
    where:
      userId = 'id'
      credential = getCredential()

  }

  def "error in match users"() {

    when:
      def result = service.validateUserCredential( credential, userId )
    then:
      1 * userService.findById( _ as String ) >> new User( id:"idd" )
      result == null
    where:
      userId = 'idd'
      credential = getCredential()

  }

  def "instance 'user' not found"() {

    when:
      service.validateUserCredential( credential, userId )
    then:
      1 * userService.findById( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'user.not.found'
    where:
      userId = 'id'
      credential = getCredential()

  }

  def "parameter 'credential' is null"() {

    when:
      service.validateUserCredential( credential, userId )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.validateUserCredential.credential.null'
    where:
      credential = null
      userId = "id"

  }

  def "parameter 'id' is null"() {

    when:
      service.validateUserCredential( credential, userId )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.validateUserCredential.userId.null'
    where:
      credential = getCredential()
      userId = null

  }

  Credential getCredential(){
    new Credential(
        id: "id",
        user: new User( id:"id" )
        )
  }

}
