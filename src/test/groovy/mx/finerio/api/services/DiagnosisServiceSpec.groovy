package mx.finerio.api.services

import mx.finerio.api.domain.Category
import mx.finerio.api.domain.SuggestedExpenses
import mx.finerio.api.domain.repository.SuggestedExpensesRepository
import mx.finerio.api.domain.repository.TransactionRepository
import mx.finerio.api.dtos.DiagnosisDto
import mx.finerio.api.dtos.MonthTransactionsDiagnosisDto
import mx.finerio.api.services.imp.DiagnosisServiceImp
import spock.lang.Specification

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.ZonedDateTime

class DiagnosisServiceSpec extends Specification{
    public static final Date TWO_MONTHS = Date.from(ZonedDateTime.now().minusMonths(2).toInstant())
    public static final Date NOW = new Date()


    DiagnosisService diagnosisService = new DiagnosisServiceImp()

    def setup(){
        diagnosisService.categoryService = Mock(CategoryService)
        diagnosisService.transactionRepository = Mock(TransactionRepository)
        diagnosisService.suggestedExpensesRepository = Mock(SuggestedExpensesRepository)
    }

    def "Should get an analysis"(){
        given:

        SuggestedExpenses suggestedExpenses = new SuggestedExpenses()
        suggestedExpenses.suggestedPercentage = 0.9

        Category parentCategory = new Category()
        parentCategory.with {
            id = 666
            name = 'parent one'
        }
        Category subCategory = new Category()
        subCategory.with {
            id = 123
            name = 'child one'
            parent = parentCategory
        }


        List transactionChargeNow = [1 ,"1", "cleaned", 1000, true,  new Timestamp(NOW.getTime()), subCategory.id ,false, 1000  ]


        List transactionChargeTwoMonths = [2 ,"1", "cleaned", 200, true,   new Timestamp(TWO_MONTHS.getTime()), subCategory.id ,false, 1000  ]

        List transactionIncomeNow = [3 ,"1", "cleaned", 50000, false, new Timestamp(NOW.getTime()), subCategory.id ,false, 50000  ]

        List transactionIncomeTwoMonths = [4 ,"1", "cleaned", 10000, false, new Timestamp(TWO_MONTHS.getTime()), subCategory.id ,false, 50000  ]


        when:

        1 * diagnosisService.transactionRepository.findAllByCustomerId(_ as Long) >> [transactionChargeNow, transactionChargeTwoMonths, transactionIncomeNow, transactionIncomeTwoMonths]
        1 * diagnosisService.categoryService.findAll() >> [parentCategory, subCategory]
        2 * diagnosisService.categoryService.findOne(_ as String) >> subCategory
        2 * diagnosisService.suggestedExpensesRepository.findByCategoryAndIncome(_ as Category,_ as BigDecimal) >> suggestedExpenses

        DiagnosisDto response = diagnosisService.getDiagnosisByCustomer(1L, null)

        then:
        assert response.averageIncome == 30000
        assert response.data.size() == 2
        def nowDate = new SimpleDateFormat("yyyy-MM").format(new Timestamp(NOW.getTime()))
        def twoMonthsDate = new SimpleDateFormat("yyyy-MM").format(new Timestamp(TWO_MONTHS.getTime()))

        MonthTransactionsDiagnosisDto thisMonthTransactions = response.data.find {it.date == nowDate.toString()}
        MonthTransactionsDiagnosisDto twoMonthTransactions = response.data.find {it.date == twoMonthsDate.toString()}

        assert thisMonthTransactions, twoMonthTransactions
        assert thisMonthTransactions.categories.size() == 1
        assert thisMonthTransactions.categories.first().categoryId == parentCategory.id
        assert thisMonthTransactions.categories.first().spent == 1000
        assert thisMonthTransactions.categories.first().average == 1000
        assert thisMonthTransactions.categories.first().subcategories.size() == 1
        assert thisMonthTransactions.categories.first().subcategories.first().amount ==1000
        assert thisMonthTransactions.categories.first().subcategories.first().categoryId == subCategory.id


        assert twoMonthTransactions.categories.size() == 1
        assert twoMonthTransactions.categories.first().categoryId == parentCategory.id
        assert twoMonthTransactions.categories.first().spent == 200
        assert twoMonthTransactions.categories.first().average == 200
        assert twoMonthTransactions.categories.first().subcategories.size() == 1
        assert twoMonthTransactions.categories.first().subcategories.first().amount ==200
        assert twoMonthTransactions.categories.first().subcategories.first().categoryId == subCategory.id




    }


}
