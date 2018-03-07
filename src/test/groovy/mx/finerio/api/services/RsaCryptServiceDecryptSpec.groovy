package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class RsaCryptServiceDecryptSpec extends Specification {

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

  def "parameter 'cryptedText' is null"() {

    when:
      service.decrypt( text )
    then:
      BadImplementationException e = thrown()
      e.message == 'rsaCryptService.decrypt.cryptedText.null'
    where:
      text = null

  }

  def "parameter 'cryptedText' is blank"() {

    when:
      service.decrypt( text )
    then:
      BadImplementationException e = thrown()
      e.message == 'rsaCryptService.decrypt.cryptedText.null'
    where:
      text = ''

  }

}
