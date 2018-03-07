package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class RsaCryptServiceEncryptSpec extends Specification {

  def service = new RsaCryptService()

  def "invoking method successfully"() {

    when:
      def encrypted = service.encrypt( text )
      def decrypted = service.decrypt( encrypted )
    then:
      text == decrypted
    where:
      text = 'Hello world!'

  }

  def "parameter 'text' is null"() {

    when:
      service.encrypt( text )
    then:
      BadImplementationException e = thrown()
      e.message == 'rsaCryptService.encrypt.text.null'
    where:
      text = null

  }

  def "parameter 'text' is blank"() {

    when:
      service.encrypt( text )
    then:
      BadImplementationException e = thrown()
      e.message == 'rsaCryptService.encrypt.text.null'
    where:
      text = ''

  }

}
