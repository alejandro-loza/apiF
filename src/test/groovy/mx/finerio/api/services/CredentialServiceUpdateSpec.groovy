package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.dtos.CredentialUpdateDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceUpdateSpec extends Specification {

  def service = new CredentialService()

  def bankConnectionService = Mock( BankConnectionService )
  def cryptService = Mock( CryptService )
  def financialInstitutionService = Mock( FinancialInstitutionService )
  def securityService = Mock( SecurityService )
  def scraperService = Mock( DevScraperService )
  def credentialRepository = Mock( CredentialRepository )

  def setup() {

    service.bankConnectionService = bankConnectionService
    service.cryptService = cryptService
    service.financialInstitutionService = financialInstitutionService
    service.securityService = securityService
    service.scraperService = scraperService
    service.credentialRepository = credentialRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.update( id, credentialUpdateDto )
    then:
      2 * securityService.getCurrent() >> client
      2 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer( client: client ),
          user: new User(), institution: new FinancialInstitution( id: 1L ) )
      1 * financialInstitutionService.findOneAndValidate( _ as Long )
      1 * cryptService.encrypt( _ as String ) >>
          [ message: 'message', iv: 'iv' ]
      2 * credentialRepository.save( _ as Credential ) >>
          new Credential( id: 'id' )
      1 * bankConnectionService.create( _ as Credential )
      1 * scraperService.requestData( _ as Map )
      result instanceof Credential
    where:
      id = 'uuid'
      credentialUpdateDto = getCredentialUpdateDto()
      client = new Client( id: 1 )

  }

  def "parameter 'id' is null"() {

    when:
      service.update( id, credentialUpdateDto )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.update.id.null'
    where:
      id = null
      credentialUpdateDto = getCredentialUpdateDto()

  }

  def "parameter 'id' is blank"() {

    when:
      service.update( id, credentialUpdateDto )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.update.id.null'
    where:
      id = ''
      credentialUpdateDto = getCredentialUpdateDto()

  }

  def "parameter 'credentialUpdateDto' is null"() {

    when:
      service.update( id, credentialUpdateDto )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.update.credentialUpdateDto.null'
    where:
      id = 'uuid'
      credentialUpdateDto = null

  }

  private CredentialUpdateDto getCredentialUpdateDto() throws Exception {

    new CredentialUpdateDto(
      password: 'password',
      securityCode: 'securityCode'
    )

  }

}
