package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Country
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.CountryRepository
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.services.imp.FinancialInstitutionServiceImp
import mx.finerio.api.validation.FinancialInstitutionUpdateCommand
import spock.lang.Specification


class FinancialInstitutionServiceUpdateFieldsSpec extends Specification {

    FinancialInstitutionService financialInstitutionService = new FinancialInstitutionServiceImp()

    def listService = Mock( ListService )
    def countryRepository = Mock( CountryRepository )
    def financialInstitutionRepository = Mock( FinancialInstitutionRepository )

    def setup() {

        financialInstitutionService.listService = listService
        financialInstitutionService.countryRepository = countryRepository
        financialInstitutionService.financialInstitutionRepository = financialInstitutionRepository
        financialInstitutionService.customerService = Mock(CustomerService)
        financialInstitutionService.securityService = Mock(SecurityService)
    }

    def 'Should not update an financial institution on provider not found '() {
        given:'an financial entity command request body'
        FinancialInstitutionUpdateCommand cmd = new FinancialInstitutionUpdateCommand()
        cmd.with{
            provider = 'wrong'
            status = FinancialInstitution.Status.ACTIVE
            institutionType = FinancialInstitution.InstitutionType.PERSONAL
        }

        when:
        financialInstitutionService.update(cmd, 1L)

        then:
        InstanceNotFoundException e = thrown()
        e.message == 'financialInstitution.not.found'

    }

    def 'Should not update an financial institution on not found status'(){
        given:'an financial entity command request body'
        FinancialInstitutionUpdateCommand cmd = new FinancialInstitutionUpdateCommand()
        cmd.with {
            status = 'wrong one'
            provider =  FinancialInstitution.Provider.SATWS
            institutionType = FinancialInstitution.InstitutionType.PERSONAL
        }

        when:
        financialInstitutionService.update(cmd, 1L)

        then:
        InstanceNotFoundException e = thrown()
        e.message == 'financialInstitution.not.found'

    }

    def 'Should not update an financial institution on not found institution type'() {
        given:'an financial entity command request body'
        FinancialInstitutionUpdateCommand cmd = new FinancialInstitutionUpdateCommand()
        cmd.with {
            status = FinancialInstitution.Status.ACTIVE
            provider =  FinancialInstitution.Provider.SATWS
            institutionType = "wrong"
        }

        when:
        financialInstitutionService.update(cmd, 1L)

        then:
        InstanceNotFoundException e = thrown()
        e.message == 'financialInstitution.not.found'

    }

    def 'Should update an financial entity'(){
        given:
        def client = new Client()

        Customer customer = new Customer()
        customer.with {
            customer.client =  client
        }
        FinancialInstitutionUpdateCommand cmd = new FinancialInstitutionUpdateCommand()
        cmd.with{
            provider = FinancialInstitution.Provider.SCRAPER_V1
            status = FinancialInstitution.Status.ACTIVE
            institutionType = FinancialInstitution.InstitutionType.PERSONAL
        }

        and:
        FinancialInstitution institutionToUpdate = new FinancialInstitution()
        institutionToUpdate.with {
            code = '123'
            internalCode = '456'
            description = 'description'
            name = 'name'
            provider = FinancialInstitution.Provider.SATWS
            status = FinancialInstitution.Status.ACTIVE
            institutionType = FinancialInstitution.InstitutionType.PERSONAL
            country = new Country()
            institutionToUpdate.customer = customer
            dateCreated = new Date()
            version = 0
        }

        when:
        1 * financialInstitutionService.financialInstitutionRepository.findOne( _ as Long ) >> institutionToUpdate
        1 * financialInstitutionService.securityService.getCurrent() >> client
        1 * financialInstitutionService.financialInstitutionRepository.findByCodeAndCustomerAndDateDeletedIsNull(_ as String, _ as Customer) >> null
        1 * financialInstitutionService.financialInstitutionRepository.save(_ as FinancialInstitution) >> new FinancialInstitution()

        def response = financialInstitutionService.update( cmd, 1L)

        then:
        assert response
    }



}
