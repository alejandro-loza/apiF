package mx.finerio.api.services

import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CryptServiceDecryptSpec extends Specification {

  def service = new CryptService( 'A' * 64 )

  def "invoking method successfully"() {

    when:
      def result = service.decrypt( value, iv )
    then:
      result instanceof String
      result == 'MyValue'
    where:
      value = '74a73cb2c1ed65d3b78d5d0a3227af183f01e4ed96b19e'
      iv = 'd28ed0b0abd60b5ded073c77'

  }

  def "parameter 'value' is null"() {

    when:
      service.decrypt( value, iv )
    then:
      BadImplementationException e = thrown()
      e.message == 'cryptService.decrypt.value.null'
    where:
      value = null
      iv = 'MyIv'

  }

  def "parameter 'value' is blank"() {

    when:
      service.decrypt( value, iv )
    then:
      BadImplementationException e = thrown()
      e.message == 'cryptService.decrypt.value.null'
    where:
      value = ''
      iv = 'MyIv'

  }

  def "parameter 'iv' is null"() {

    when:
      service.decrypt( value, iv )
    then:
      BadImplementationException e = thrown()
      e.message == 'cryptService.decrypt.iv.null'
    where:
      value = 'MyValue'
      iv = null

  }

  def "parameter 'iv' is blank"() {

    when:
      service.decrypt( value, iv )
    then:
      BadImplementationException e = thrown()
      e.message == 'cryptService.decrypt.iv.null'
    where:
      value = 'MyValue'
      iv = ''

  }

}
