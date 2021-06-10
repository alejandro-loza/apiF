package mx.finerio.api.services

import mx.finerio.api.domain.BankConnection
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.BankConnectionRepository
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.dtos.SuccessCallbackDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceRequestDataSpec extends Specification {

  def service = new CredentialService()

  def bankConnectionService = Mock( BankConnectionService )
  def credentialStatusHistoryService = Mock( CredentialStatusHistoryService )
  def scraperService = Mock( DevScraperService )
  def scraperCallbackService = Mock( ScraperCallbackService )
  def scraperV2TokenService = Mock( ScraperV2TokenService )
  def securityService = Mock( SecurityService )
  def credentialRepository = Mock( CredentialRepository )
  def scraperV2Service = Mock( ScraperV2Service )
  def cryptService = Mock( CryptService )

  def setup() {

    service.bankConnectionService = bankConnectionService
    service.credentialStatusHistoryService = credentialStatusHistoryService
    service.scraperService = scraperService
    service.scraperCallbackService = scraperCallbackService
    service.scraperV2TokenService = scraperV2TokenService
    service.securityService = securityService
    service.credentialRepository = credentialRepository
    service.scraperV2Service = scraperV2Service
    service.cryptService = cryptService

  }

  def "invoking method successfully"() {

    when:
      service.requestData( credentialId )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ),
          institution: new FinancialInstitution( provider: FinancialInstitution.Provider.SCRAPER_V2 ),
          user: new User() )
      1 * credentialRepository.save( _ as Credential )
      1 * bankConnectionService.create( _ as Credential )
      1 * credentialStatusHistoryService.create( _ as Credential )
    where:
      credentialId = UUID.randomUUID().toString()
      client = new Client( id: 1 )

  }

  def "invoking method successfully with status PARTIALLY_ACTIVE"() {

    when:
    service.requestData( credentialId )
    then:
    1 * securityService.getCurrent() >> client
    1 * credentialRepository.findOne( _ as String ) >>
            new Credential( id: credentialId, customer: new Customer(
                    client: client ),
                    institution: new FinancialInstitution(status: FinancialInstitution.Status.PARTIALLY_ACTIVE),
                    user: new User() )
    1 * credentialRepository.save( _ as Credential )
    1 * scraperCallbackService.processSuccess(_ as SuccessCallbackDto)
    1 * scraperCallbackService.postProcessSuccess( _ as Credential )
    where:
    credentialId = UUID.randomUUID().toString()
    client = new Client( id: 1 )

  }

  def "invoking method successfully (credential updated an hour ago)"() {

    when:
      service.requestData( credentialId )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ),
          institution: new FinancialInstitution( provider: FinancialInstitution.Provider.SCRAPER_V2 ),
          user: new User() )
      1 * bankConnectionService.findLast( _ as Credential ) >>
          new BankConnection( startDate: getStartDate( 1 ) )
    where:
      credentialId = UUID.randomUUID().toString()
      client = new Client( id: 1 )

  }

  def "invoking method successfully (credential updated a day ago)"() {

    when:
      service.requestData( credentialId )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ),
          institution: new FinancialInstitution( provider: FinancialInstitution.Provider.SCRAPER_V2 ),
          user: new User() )
      1 * bankConnectionService.findLast( _ as Credential ) >>
          new BankConnection( startDate: getStartDate( 24 ) )
      1 * credentialRepository.save( _ as Credential )
      1 * bankConnectionService.create( _ as Credential )
      1 * credentialStatusHistoryService.create( _ as Credential )
    where:
      credentialId = UUID.randomUUID().toString()
      client = new Client( id: 1 )

  }

  def "invoking method successfully (interactive bank)"() {

    when:
      service.requestData( credentialId )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer(
          client: client ),
          institution: new FinancialInstitution( code: 'BBVA', provider: FinancialInstitution.Provider.SCRAPER_V2 ),
          user: new User() )
      1 * credentialRepository.save( _ as Credential )
      1 * bankConnectionService.create( _ as Credential )
      1 * credentialStatusHistoryService.create( _ as Credential )
    where:
      credentialId = UUID.randomUUID().toString()
      client = new Client( id: 1 )

  }

  def "parameter 'credentialId' is null"() {

    when:
      service.requestData( credentialId )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findOne.id.null'
    where:
      credentialId = null

  }

  def "parameter 'credentialId' is blank"() {

    when:
      service.requestData( credentialId )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.findOne.id.null'
    where:
      credentialId = ''

  }

  def "parameter 'credentialId' is invalid"() {

    when:
      service.requestData( credentialId )
    then:
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'credential.not.found'
    where:
      credentialId = UUID.randomUUID().toString()
      client = new Client( id: 1 )

  }

  private Date getStartDate( int days ) throws Exception {

    def cal = Calendar.instance
    cal.add( Calendar.HOUR, -days )
    cal.time

  }

}
