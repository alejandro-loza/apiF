package mx.finerio.api.services

import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.CountryRepository
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.services.imp.FinancialInstitutionServiceImp
import spock.lang.Specification

class FinancialInstitutionServiceDeleteFieldsSpec extends Specification {

    FinancialInstitutionService financialInstitutionService = new FinancialInstitutionServiceImp()

    def listService = Mock( ListService )
    def countryRepository = Mock( CountryRepository )
    def financialInstitutionRepository = Mock( FinancialInstitutionRepository )

    def setup() {

        financialInstitutionService.listService = listService
        financialInstitutionService.countryRepository = countryRepository
        financialInstitutionService.financialInstitutionRepository = financialInstitutionRepository
        financialInstitutionService.customerService = Mock(CustomerService)
    }

  def "Should logic delete a field"() {
     given:
     FinancialInstitution institution = new FinancialInstitution()

     when:
     1 * financialInstitutionService.financialInstitutionRepository.save(_ as FinancialInstitution) >> new FinancialInstitution()

     financialInstitutionService.delete(institution)

     then:
     assert institution.dateDeleted

  }




}
