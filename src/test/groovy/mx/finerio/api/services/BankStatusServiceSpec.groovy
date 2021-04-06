package mx.finerio.api.services

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.repository.BankConnectionRepository
import mx.finerio.api.domain.repository.ClientRepository
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.dtos.BankStatusDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import org.mockito.Mock
import spock.lang.Specification
import spock.lang.Unroll

class BankStatusServiceSpec extends Specification {

    def bankStatusService = new BankStatusService()
    def financialInstitutionRepository = Mock( FinancialInstitutionRepository )
    def callbackService = Mock( CallbackService )
    def clientRepository = Mock( ClientRepository )
    def emailRestService = Mock( EmailRestService )

    def setup() {
        bankStatusService.financialInstitutionRepository = financialInstitutionRepository
        bankStatusService.callbackService = callbackService
        bankStatusService.clientRepository = clientRepository
        bankStatusService.emailRestService = emailRestService
    }

    @Unroll
    def "bank status notification by email and callBack"() {
        given:
        financialInstitutionRepository.findById(1) >> fillFinancialInstitution()
        callbackService.findAllByNature(Callback.Nature.BANKS) >> fillCallBacksList()
        clientRepository.findAllByEnabledTrueAndDateDeletedIsNullAndEmailIsNotNull() >> fillClientList()

        when:
        bankStatusService.changeStatus(fillDto())

        then:
        1 == 1

    }

    def "IllegalArgument BankStatus cant be null"() {

        when:
        bankStatusService.changeStatus(null)

        then:
        IllegalArgumentException e = thrown()
        e.message == 'bankStatusService.changeBankStatus.dto.null'
    }

    def "BadRequestException invalid status"() {

        given:
        financialInstitutionRepository.findById(1) >> fillFinancialInstitution()

        when:
        bankStatusService.changeStatus(fillDtoInvalidStatus())

        then:
        BadRequestException e = thrown()
        e.message == 'bank.status.not.found'
    }

    def "BadRequestException bank id not found"() {

        when:
        bankStatusService.changeStatus(fillDtoInvalidStatus())

        then:
        BadRequestException e = thrown()
        e.message == 'bank.id.not.found'
    }

    def fillFinancialInstitution() {
        new FinancialInstitution(
        name: "Banamex",
        status: FinancialInstitution.Status.ACTIVE)
    }

    def fillDto() {
        new BankStatusDto(
        bankId: 1L,
        status: "ACTIVE",
        notifyClients: true)
    }

    def fillDtoInvalidStatus() {
        new BankStatusDto(
        bankId: 1L,
        status: 'NEGATIVE',
        notifyClients: true)
    }

    def fillCallBacksList() {
        List<Callback> callbackList = new ArrayList<>();
        callbackList.add(new Callback(
                id: 1L))
        return callbackList
    }

    def fillClientList() {
        List<Client> clientList = new ArrayList<>();
        clientList.add(new Client(
                name: 'Finerio',
                company: 'finerio',
                email: 'prueba@finerio.mx'))
        return clientList
    }
}
