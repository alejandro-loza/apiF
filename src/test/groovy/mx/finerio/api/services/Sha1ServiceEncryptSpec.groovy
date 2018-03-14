package mx.finerio.api.services

import javax.xml.bind.DatatypeConverter

import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class Sha1ServiceEncryptSpec extends Specification {

  def service = new Sha1Service()

  def "everything was OK"() {

    when:
      def result = service.encrypt( input )
    then:
      result instanceof byte[]
      result.size() == 20
      DatatypeConverter.printHexBinary( result ).size() == 40
    where:
      input = 'Hello World'

  }

  def "parameter 'input' is null"() {

    when:
      service.encrypt( input )
    then:
      BadImplementationException e = thrown()
      e.message == 'sha1Service.encrypt.input.null'
    where:
      input = null

  }

  def "parameter 'input' is blank"() {

    when:
      service.encrypt( input )
    then:
      BadImplementationException e = thrown()
      e.message == 'sha1Service.encrypt.input.null'
    where:
      input = ''

  }

}
