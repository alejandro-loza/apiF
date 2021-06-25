package mx.finerio.api.services

import mx.finerio.api.domain.Country
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.CountryRepository
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.validation.FinancialInstitutionCreateCommand
import spock.lang.Specification
import mx.finerio.api.services.imp.FinancialInstitutionServiceImp
import mx.finerio.api.exceptions.BadRequestException

class FinancialInstitutionServiceCreateSpec extends Specification {

  FinancialInstitutionService financialInstitutionService = new FinancialInstitutionServiceImp()

  def listService = Mock( ListService )
  def countryRepository = Mock( CountryRepository )
  def financialInstitutionRepository = Mock( FinancialInstitutionRepository )

  def setup() {

    financialInstitutionService.listService = listService
    financialInstitutionService.countryRepository = countryRepository
    financialInstitutionService.financialInstitutionRepository = financialInstitutionRepository
    financialInstitutionService.securityService = Mock(SecurityService)
    financialInstitutionService.customerService = Mock(CustomerService)
  }

  def 'Should not save an financial institution on provider not found '() {
    given:'an financial entity command request body'
    FinancialInstitutionCreateCommand cmd = new FinancialInstitutionCreateCommand()
    cmd.with{
      provider = 'wrong'
      status = FinancialInstitution.Status.ACTIVE
      institutionType = FinancialInstitution.InstitutionType.PERSONAL
      customerId = 123
    }

    when:
    financialInstitutionService.create(cmd)

    then:
    BadRequestException e = thrown()
    e.message == 'financialInstitution.provider.invalid'

  }

  def 'Should not save an financial institution on empty cmd'(){
    given:'an financial entity command request body'
    FinancialInstitutionCreateCommand cmd = new FinancialInstitutionCreateCommand()

    when:
    financialInstitutionService.create(cmd)

    then:
    BadRequestException e = thrown()

  }

  def 'Should not save an financial institution on not found status'(){
    given:'an financial entity command request body'
    FinancialInstitutionCreateCommand cmd = new FinancialInstitutionCreateCommand()
    cmd.with {
      status = 'wrong one'
      provider =  FinancialInstitution.Provider.SATWS
      institutionType = FinancialInstitution.InstitutionType.PERSONAL
    }

    when:
    financialInstitutionService.create(cmd)

    then:
    BadRequestException e = thrown()
    e.message == 'financialInstitution.status.invalid'

  }

  def 'Should not save an financial institution on not found institution type'() {
    given:'an financial entity command request body'
    FinancialInstitutionCreateCommand cmd = new FinancialInstitutionCreateCommand()
    cmd.with {
      status = FinancialInstitution.Status.ACTIVE
      provider =  FinancialInstitution.Provider.SATWS
      institutionType = "wrong"
    }

    when:
    financialInstitutionService.create(cmd)

    then:
    BadRequestException e = thrown()
    e.message == 'financialInstitution.institutionType.invalid'

  }

  def 'Should create a financial institution'() {
    given:'an financial entity command request body'
    FinancialInstitutionCreateCommand cmd = new FinancialInstitutionCreateCommand()
    cmd.with {
      status = FinancialInstitution.Status.ACTIVE
      provider =  FinancialInstitution.Provider.SATWS
      institutionType = FinancialInstitution.InstitutionType.PERSONAL
      country = "the most awesomest ever MEXICO"
      customerId = 123
    }

    when:
    1 * financialInstitutionService.securityService.getCurrent() >> new Client()
    1 * financialInstitutionService.customerService.findOne(_ as Long, _ as Client) >> new Customer()
    1 * financialInstitutionService.financialInstitutionRepository.findByCodeAndCustomerAndDateDeletedIsNull(_ as String, _ as Customer) >> null
    1 * financialInstitutionService.countryRepository.findOneByCode(_ as String) >> new Country()
    1 * financialInstitutionService.financialInstitutionRepository.save(_ as FinancialInstitution) >> new FinancialInstitution()

    def response = financialInstitutionService.create(cmd)

    then:
    assert response

  }

  def 'Should not create a financial institution on not found country'() {
    given:'an financial entity command request body'
    FinancialInstitutionCreateCommand cmd = new FinancialInstitutionCreateCommand()
    cmd.with {
      status = FinancialInstitution.Status.ACTIVE
      provider =  FinancialInstitution.Provider.SATWS
      institutionType = FinancialInstitution.InstitutionType.PERSONAL
      country = "the most awesomest ever MEXICO"
      customerId = 123
    }

    when:
    1 * financialInstitutionService.securityService.getCurrent() >> new Client()
    1 * financialInstitutionService.customerService.findOne(_ as Long, _ as Client) >> new Customer()
    1 * financialInstitutionService.financialInstitutionRepository.findByCodeAndCustomerAndDateDeletedIsNull(_ as String, _ as Customer) >> null
    1 * financialInstitutionService.countryRepository.findOneByCode(_ as String) >> null
    0 * financialInstitutionService.financialInstitutionRepository.save(_ as FinancialInstitution)

    financialInstitutionService.create(cmd)

    then:
    BadRequestException e = thrown()
    e.message == 'country.not.found'

  }

  def 'Should not create a financial institution that already exist'() {
    given:'an financial entity command request body'
    FinancialInstitutionCreateCommand cmd = new FinancialInstitutionCreateCommand()
    cmd.with {
      status = FinancialInstitution.Status.ACTIVE
      provider =  FinancialInstitution.Provider.SATWS
      institutionType = FinancialInstitution.InstitutionType.PERSONAL
      country = "the most awesomest ever MEXICO"
      customerId = 123
    }

    when:
    1 * financialInstitutionService.securityService.getCurrent() >> new Client()
    1 * financialInstitutionService.customerService.findOne(_ as Long, _ as Client) >> new Customer()
    1 * financialInstitutionService.financialInstitutionRepository.findByCodeAndCustomerAndDateDeletedIsNull(_ as String, _ as Customer) >>   new FinancialInstitution()
    0 * financialInstitutionService.countryRepository.findOneByCode(_ as String)
    0 * financialInstitutionService.financialInstitutionRepository.save(_ as FinancialInstitution)

    financialInstitutionService.create(cmd)

    then:
    BadRequestException e = thrown()
    e.message == 'financialInstitution.code.nonUnique'

  }

}
