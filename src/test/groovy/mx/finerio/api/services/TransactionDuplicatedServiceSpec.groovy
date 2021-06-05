package mx.finerio.api.services

import mx.finerio.api.contants.Constants
import mx.finerio.api.domain.Account
import mx.finerio.api.domain.Category
import mx.finerio.api.domain.Transaction
import mx.finerio.api.domain.repository.TransactionRepository
import spock.lang.Specification

import java.sql.Timestamp

class TransactionDuplicatedServiceSpec extends Specification {

    def service = new TransactionDuplicatedService()
    def transactionsApiService = Mock( TransactionsApiService )
    def transactionRepository = Mock( TransactionRepository )

    def setup() {
        service.transactionsApiService = transactionsApiService
        service.transactionRepository = transactionRepository
    }

    def "isDepositCreditAccount successfully"() {

        given:
        def transaction = new Transaction( charge: false, duplicated: false,
                account: new Account( nature: Constants.CREDIT ) )
        when:
        service.duplicatedTransaction( transaction )
        then:
        transaction.duplicated == true

    }

    def "isValidAtmTrans successfully"() {

        given:
        def transaction = new Transaction( charge: true, duplicated: false,
                category: new Category( id: "atmId" ),
                account: new Account( nature: Constants.CREDIT ),
                amount: 100 )
        service.atmId = "atmId"
        when:
        service.duplicatedTransaction( transaction )
        then:
        transaction.duplicated == true

    }

    def "duplicated transactions successfully"() {

        given:
        def transaction = new Transaction(id: 1L, charge: true, duplicated: false,
                bankDate: new Timestamp( System.currentTimeMillis() ),
                category: new Category( id: "0001" ),
                account: new Account( id: 1L, nature: Constants.CREDIT ),
                amount: new BigDecimal( 99 ) )
        when:
        service.duplicatedTransaction( transaction )
        then:
        1 * transactionRepository.findAllByAccountAndAmountAndChargeAndDateDeletedIsNull(
                _ as Account, _ as BigDecimal, _ as Boolean ) >> fillTransactions()
        1 * transactionsApiService.find( _ as Map ) >> [ results: [ [reason: [data:"numbers"], similarity: [percent:91.5] ] ] ]
        transaction.duplicated == true

    }

    private List fillTransactions() {
        def transaction = new Transaction( id: 2L, charge: true, duplicated: false,
                bankDate: new Timestamp( System.currentTimeMillis() ),
                category: new Category( id: "0001" ),
                account: new Account( nature: Constants.CREDIT ),
                amount: new BigDecimal( 99 ) )
        List transactions = new ArrayList<>()
        transactions.add( transaction )
        return transactions
    }

}
