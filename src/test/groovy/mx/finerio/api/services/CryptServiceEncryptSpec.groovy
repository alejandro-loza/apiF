package mx.finerio.api.services

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CryptServiceEncryptSpec extends Specification {

  def service = new CryptService( 'A' * 64 )

  def "invoking method successfully"() {

    when:
      def result = service.encrypt( value )
    then:
      result instanceof Map
      result.message != null
      result.iv != null
    where:
      value = 'MyValue'

  }

  def "parameter 'value' is null"() {

    when:
      service.encrypt( value )
    then:
      BadImplementationException e = thrown()
      e.message == 'cryptService.encrypt.value.null'
    where:
      value = null

  }

  def "parameter 'value' is blank"() {

    when:
      service.encrypt( value )
    then:
      BadImplementationException e = thrown()
      e.message == 'cryptService.encrypt.value.null'
    where:
      value = ''

  }

}
