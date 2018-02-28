package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.User
import mx.finerio.api.domain.repository.CredentialRepository
import mx.finerio.api.dtos.CredentialDto
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialServiceCreateSpec extends Specification {

  def service = new CredentialService()

  def bankConnectionService = Mock( BankConnectionService )
  def cryptService = Mock( CryptService )
  def customerService = Mock( CustomerService )
  def financialInstitutionService = Mock( FinancialInstitutionService )
  def securityService = Mock( SecurityService )
  def scraperService = Mock( DevScraperService )
  def userService = Mock( UserService )
  def credentialRepository = Mock( CredentialRepository )

  def setup() {

    service.bankConnectionService = bankConnectionService
    service.cryptService = cryptService
    service.customerService = customerService
    service.financialInstitutionService = financialInstitutionService
    service.securityService = securityService
    service.scraperService = scraperService
    service.userService = userService
    service.credentialRepository = credentialRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.create( credentialDto )
    then:
      1 * customerService.findOne( _ as Long ) >> new Customer()
      1 * financialInstitutionService.findOneAndValidate( _ as Long ) >>
          new FinancialInstitution()
      1 * credentialRepository.
          findByCustomerAndInstitutionAndUsernameAndDateDeleted(
          _ as Customer, _ as FinancialInstitution, _ as String, null )
      1 * userService.getApiUser() >> new User()
      1 * cryptService.encrypt( _ as String ) >>
          [ message: 'message', iv: 'iv' ]
      2 * credentialRepository.save( _ as Credential ) >>
          new Credential( id: 'id' )
      1 * securityService.getCurrent() >> client
      1 * credentialRepository.findOne( _ as String ) >>
          new Credential( customer: new Customer( client: client ),
          user: new User(), institution: new FinancialInstitution() )
      1 * bankConnectionService.create( _ as Credential )
      1 * scraperService.requestData( _ as Map )
      result instanceof Credential
    where:
      credentialDto = getCredentialDto()
      client = new Client( id: 1L )

  }

  def "parameter 'credentialDto' is null"() {

    when:
      service.create( credentialDto )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialService.create.credentialDto.null'
    where:
      credentialDto = null

  }

  def "instance already exists"() {

    when:
      service.create( credentialDto )
    then:
      1 * customerService.findOne( _ as Long ) >> new Customer()
      1 * financialInstitutionService.findOneAndValidate( _ as Long ) >>
          new FinancialInstitution()
      1 * credentialRepository.
          findByCustomerAndInstitutionAndUsernameAndDateDeleted(
          _ as Customer, _ as FinancialInstitution, _ as String, null ) >>
          new Credential()
      BadRequestException e = thrown()
      e.message == 'credential.create.exists'
    where:
      credentialDto = getCredentialDto()

  }

  private CredentialDto getCredentialDto() throws Exception {

    new CredentialDto(
      customerId: 1L,
      bankId: 1L,
      username: 'username',
      password: 'password'
    )

  }

}
