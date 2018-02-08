package mx.finerio.api.services

import mx.finerio.api.domain.Credential
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class CredentialServiceGetFieldsSpec extends Specification {

  def service = new CredentialService()

  def "invoking method successfully"() {

    when:
      def result = service.getFields( credential )
    then:
      result instanceof Map
      result.id != null
      result.username != null
      result.status != null
      result.dateCreated != null
    where:
      credential = getCredential()

  }

  def "parameter 'credential' is null"() {

    when:
      service.getFields( credential )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.getFields.credential.null'
    where:
      credential = null

  }

  private Credential getCredential() throws Exception {

    new Credential(
      id: 1L,
      username: 'username',
      status: Credential.Status.ACTIVE,
      dateCreated: new Date()
    )

  }

}
