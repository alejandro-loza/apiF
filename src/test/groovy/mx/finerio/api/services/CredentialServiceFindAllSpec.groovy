package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.dtos.ListDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException

import org.springframework.data.jpa.repository.JpaRepository

import spock.lang.Specification

class CredentialServiceFindAllSpec extends Specification {

  def service = new CredentialService()

  def listService = Mock( ListService )
  def customerService = Mock( CustomerService )
  def securityService = Mock( SecurityService )
  def credentialRepository = Mock( CredentialRepository )

  def setup() {

    service.listService = listService
    service.customerService = customerService
    service.securityService = securityService
    service.credentialRepository = credentialRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.findAll( params )
    then:
      1 * listService.validateFindAllDto( _ as ListDto, _ as Map )
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String )>>
          new Credential( customer: new Customer( client: client ) )
      1 * customerService.findOne( _ as Long ) >> new Customer( client: client )
      1 * listService.findAll( _ as ListDto, _ as JpaRepository,
          _ as Object ) >> [ data: [ new Credential(), new Credential() ],
          nextCursor: 'nextCursor' ]
      result instanceof Map
      result.nextCursor == 'nextCursor'
      result.data instanceof List
      result.data.size() == 2
      result.data[ 0 ] instanceof Credential
    where:
      params = getParams()
      client = new Client( id: 1L )

  }

  def "parameter 'params' is null"() {

    when:
      service.findAll( params )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findAll.params.null'
    where:
      params = null

  }

  def "parameter 'params.cursor' is null"() {

    given:
      params.cursor = null
    when:
      def result = service.findAll( params )
    then:
      1 * listService.validateFindAllDto( _ as ListDto, _ as Map )
      1 * customerService.findOne( _ as Long ) >> new Customer()
      1 * listService.findAll( _ as ListDto, _ as JpaRepository,
          _ as Object ) >> [ data: [ new Credential(), new Credential() ],
          nextCursor: 'nextCursor' ]
      result instanceof Map
      result.nextCursor == 'nextCursor'
      result.data instanceof List
      result.data.size() == 2
      result.data[ 0 ] instanceof Credential
    where:
      params = getParams()

  }

  def "parameter 'params.customerId' is null"() {

    given:
      params.customerId = null
    when:
      service.findAll( params )
    then:
      BadRequestException e = thrown()
      e.message == 'credential.findAll.customerId.null'
    where:
      params = getParams()

  }

  def "parameter 'params.customerId' is invalid"() {

    given:
      params.customerId = 'invalid'
    when:
      service.findAll( params )
    then:
      BadRequestException e = thrown()
      e.message == 'credential.findAll.customerId.invalid'
    where:
      params = getParams()

  }

  private Map getParams() throws Exception {

    [
      customerId: '1',
      cursor: 'cursor'
    ]

  }

}
