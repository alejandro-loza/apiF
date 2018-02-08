package mx.finerio.api.services

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

  def credentialService = Mock( CredentialService )
  def cryptService = Mock( CryptService )
  def customerService = Mock( CustomerService )
  def financialInstitutionService = Mock( FinancialInstitutionService )
  def userService = Mock( UserService )
  def credentialRepository = Mock( CredentialRepository )

  def setup() {

    service.selfReference = credentialService
    service.cryptService = cryptService
    service.customerService = customerService
    service.financialInstitutionService = financialInstitutionService
    service.userService = userService
    service.credentialRepository = credentialRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.create( credentialDto )
    then:
      1 * customerService.findOne( _ as Long ) >> new Customer()
      1 * financialInstitutionService.findOne( _ as Long ) >>
          new FinancialInstitution()
      1 * credentialRepository.findByCustomerAndInstitutionAndUsername(
          _ as Customer, _ as FinancialInstitution, _ as String )
      1 * userService.getApiUser() >> new User()
      1 * cryptService.encrypt( _ as String ) >>
          [ message: 'message', iv: 'iv' ]
      1 * credentialRepository.save( _ as Credential ) >>
          new Credential( id: 'id' )
      result instanceof Credential
      1 * credentialService.asyncRequestData( _ as String )
    where:
      credentialDto = getCredentialDto()

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
      1 * financialInstitutionService.findOne( _ as Long ) >>
          new FinancialInstitution()
      1 * credentialRepository.findByCustomerAndInstitutionAndUsername(
          _ as Customer, _ as FinancialInstitution, _ as String ) >>
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
