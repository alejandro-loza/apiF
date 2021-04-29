package mx.finerio.api.services

import mx.finerio.api.domain.BankConnection
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.dtos.CredentialUpdateDto
import mx.finerio.api.dtos.SuccessCallbackDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceUpdateSpec extends Specification {

  def service = new CredentialService()

  def bankConnectionService = Mock( BankConnectionService )
  def credentialStatusHistoryService = Mock( CredentialStatusHistoryService )
  def cryptService = Mock( CryptService )
  def financialInstitutionService = Mock( FinancialInstitutionService )
  def securityService = Mock( SecurityService )
  def scraperService = Mock( DevScraperService )
  def credentialRepository = Mock( CredentialRepository )
  def scraperV2Service = Mock( ScraperV2Service )
  def scraperCallbackService = Mock( ScraperCallbackService )

  def setup() {

    service.bankConnectionService = bankConnectionService
    service.credentialStatusHistoryService = credentialStatusHistoryService
    service.cryptService = cryptService
    service.financialInstitutionService = financialInstitutionService
    service.securityService = securityService
    service.scraperService = scraperService
    service.credentialRepository = credentialRepository
    service.scraperV2Service = scraperV2Service
    service.scraperCallbackService = scraperCallbackService

  }

  def "invoking method successfully"() {

    when:
      def result = service.update( id, credentialUpdateDto )
    then:
      2 * securityService.getCurrent() >> client
      2 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer( client: client ),
          user: new User(), institution: new FinancialInstitution( id: 1L, status: FinancialInstitution.Status.ACTIVE ) )
      2 * bankConnectionService.findLast( _ as Credential ) >> null
      1 * financialInstitutionService.findOneAndValidate( _ as Long ) >>
              new FinancialInstitution( id: 1L, status: FinancialInstitution.Status.ACTIVE )
      1 * cryptService.encrypt( _ as String ) >>
          [ message: 'message', iv: 'iv' ]
      2 * credentialRepository.save( _ as Credential ) >>
          new Credential( id: 'id' )
      1 * bankConnectionService.create( _ as Credential )
      1 * credentialStatusHistoryService.create( _ as Credential )
      result instanceof Credential
    where:
      id = 'uuid'
      credentialUpdateDto = getCredentialUpdateDto()
      client = new Client( id: 1 )

  }

  def "invoking method successfully with status PARTIALLY_ACTIVE"() {

    when:
    def result = service.update( id, credentialUpdateDto )
    then:
    2 * securityService.getCurrent() >> client
    2 * credentialRepository.findOne( _ as String ) >>
            new Credential( id: 'id', customer: new Customer( client: client ),
                    user: new User(), institution: new FinancialInstitution( id: 1L,
                    status: FinancialInstitution.Status.PARTIALLY_ACTIVE ) )
    2 * bankConnectionService.findLast( _ as Credential ) >> null
    1 * financialInstitutionService.findOneAndValidate( _ as Long ) >>
            new FinancialInstitution( id: 1L, status: FinancialInstitution.Status.PARTIALLY_ACTIVE )
    1 * cryptService.encrypt( _ as String ) >>
            [ message: 'message', iv: 'iv' ]
    2 * credentialRepository.save( _ as Credential ) >>
            new Credential( id: 'id' )
    1 * scraperCallbackService.processSuccess(_ as SuccessCallbackDto)
    1 * scraperCallbackService.postProcessSuccess( _ as Credential )
    result instanceof Credential
    where:
    id = 'uuid'
    credentialUpdateDto = getCredentialUpdateDto()
    client = new Client( id: 1 )

  }

  def "invoking method successfully (credential updated an hour ago)"() {

    when:
      def result = service.update( id, credentialUpdateDto )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.save( _ as Credential ) >>
            new Credential( id: 'id', customer: new Customer( client: client ),
                    user: new User(), institution: new FinancialInstitution( id: 1L ),
                    status: Credential.Status.ACTIVE )
      1 * cryptService.encrypt( _ as String ) >>
            [ message: 'message', iv: 'iv' ]
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer( client: client ),
          user: new User(), institution: new FinancialInstitution( id: 1L ) )
      1 * bankConnectionService.findLast( _ as Credential ) >>
          new BankConnection( startDate: new Date() )
      result instanceof Credential
    where:
      id = 'uuid'
      credentialUpdateDto = getCredentialUpdateDto()
      client = new Client( id: 1 )

  }

  def "invoking method successfully (credential status == VALIDATE)"() {

    when:
      def result = service.update( id, credentialUpdateDto )
    then:
      1 * securityService.getCurrent() >> client
      1 * cryptService.encrypt( _ as String ) >>
            [ message: 'message', iv: 'iv' ]
      1 * credentialRepository.save( _ as Credential ) >>
            new Credential( id: 'id', customer: new Customer( client: client ),
                    user: new User(), institution: new FinancialInstitution( id: 1L ),
                    status: Credential.Status.VALIDATE )
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer( client: client ),
          user: new User(), institution: new FinancialInstitution( id: 1L ),
          status: Credential.Status.VALIDATE )
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
