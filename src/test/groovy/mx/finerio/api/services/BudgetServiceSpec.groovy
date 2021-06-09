package mx.finerio.api.services

import mx.finerio.api.domain.Budget
import mx.finerio.api.domain.Category
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.repository.BudgetRepository
import mx.finerio.api.domain.repository.TransactionRepository
import mx.finerio.api.dtos.pfm.BudgetDto
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.services.imp.BudgetServiceImp
import mx.finerio.api.validation.BudgetCreateCommand
import mx.finerio.api.validation.BudgetUpdateCommand
import spock.lang.Specification

import java.sql.Timestamp
import java.time.ZonedDateTime

class BudgetServiceSpec extends Specification {

    BudgetService budgetService = new BudgetServiceImp()

    def setup() {
        budgetService.budgetRepository = Mock(BudgetRepository)
        budgetService.customerService = Mock(CustomerService)
        budgetService.categoryService = Mock(CategoryService)
        budgetService.securityService = Mock(SecurityService)
        budgetService.accountService = Mock(AccountService)
        budgetService.transactionRepository = Mock(TransactionRepository)
        budgetService.credentialService = Mock(CredentialService)
    }

    def "Should create a budget"(){
        given:'a budget create command'
        BudgetCreateCommand cmd = new BudgetCreateCommand()
        cmd.with {
            customerId = 1
            categoryId = '1'
            name = 'test budget'
            amount = 100.00
            warningPercentage = 0.5
        }

        and:
        def budget = new Budget()

        when:
        def result = budgetService.create(cmd)

        then:
        1 * budgetService.customerService.findOne(_ as Long ) >> new Customer()
        1 * budgetService.categoryService.findOne(_ as String ) >> new Category()
        1 * budgetService.budgetRepository.save(_ as Budget) >> budget

        assert result == budget


    }

    def "Should create a budget dto with analysis and transactions exceeds the warning percentage"(){
        given:
        def thisMonthSecondDay = new Timestamp( Date.from(ZonedDateTime.now().withDayOfMonth(2).toInstant()).getTime())
        def twoMonthsAgo = new Timestamp(Date.from(ZonedDateTime.now().minusMonths(2).toInstant()).getTime())

        def category = new Category()
        category.id = '123'

        Customer customer = new Customer()
        customer.id = 123

        Budget budget = new Budget()
        budget.with {
            budget.category = category
            warningPercentage = 0.5
            amount = 1500
            budget.customer= customer
        }

        List transactionChargeNow = [1 ,"1", "the only one ok", 1000, true, thisMonthSecondDay, category.id ,false, 1000  ]
        List transactionChargeNowCategory = [1 ,"1", "cleaned", 1000, true, thisMonthSecondDay, 'random' ,false, 1000  ]

        List transactionChargeTwoMonths = [2 ,"1", "cleaned", 200, true, twoMonthsAgo, '123', false, 1000  ]

        List transactionIncomeNow = [3 ,"1", "cleaned", 50000, false, thisMonthSecondDay, category.id  ,false, 50000  ]

        List transactionIncomeTwoMonths = [4 ,"1", "cleaned", 10000, false, thisMonthSecondDay, category.id  ,false, 50000  ]



        when:
        def response = budgetService.crateBudgetDtoWithAnalysis(budget)

        then:
        1 * budgetService.transactionRepository.findAllByCustomerId(_ as Long) >> [transactionChargeNow, transactionChargeNowCategory, transactionChargeTwoMonths, transactionIncomeNow, transactionIncomeTwoMonths]

        assert  response instanceof BudgetDto
        assert  response.spent == 1000
        assert  response.leftToSpend == 500
        assert  response.status == BudgetDto.StatusEnum.warning

    }

    def "Should create a budget dto with analysis and transactions not exceeds the default warning percentage"(){
        given:
        def thisMonthSecondDay = new Timestamp( Date.from(ZonedDateTime.now().withDayOfMonth(2).toInstant()).getTime())
        def twoMonthsAgo = new Timestamp(Date.from(ZonedDateTime.now().minusMonths(2).toInstant()).getTime())

        def category = new Category()
        category.id = '123'

        Customer customer = new Customer()
        customer.id = 123

        Budget budget = new Budget()
        budget.with {
            budget.category = category
            warningPercentage = 0.7
            amount = 1500
            budget.customer= customer
        }

        List transactionChargeNow = [1 ,"1", "the only one ok", 1000, true, thisMonthSecondDay, category.id ,false, 1000  ]
        List transactionChargeNowCategory = [1 ,"1", "cleaned", 1000, true, thisMonthSecondDay, 'random' ,false, 1000  ]

        List transactionChargeTwoMonths = [2 ,"1", "cleaned", 200, true, twoMonthsAgo, '123', false, 1000  ]

        List transactionIncomeNow = [3 ,"1", "cleaned", 50000, false, thisMonthSecondDay, category.id  ,false, 50000  ]

        List transactionIncomeTwoMonths = [4 ,"1", "cleaned", 10000, false, thisMonthSecondDay, category.id  ,false, 50000  ]



        when:
        def response = budgetService.crateBudgetDtoWithAnalysis(budget)

        then:
        1 * budgetService.transactionRepository.findAllByCustomerId(_ as Long) >> [transactionChargeNow, transactionChargeNowCategory, transactionChargeTwoMonths, transactionIncomeNow, transactionIncomeTwoMonths]

        assert  response instanceof BudgetDto
        assert  response.spent == 1000
        assert  response.leftToSpend == 500
        assert  response.status == BudgetDto.StatusEnum.ok

    }

    def "Should get a budget"(){

        given:
        Client client = new Client()

        Customer customer = new Customer()
        customer.client = client
        Budget budget = new Budget()
        budget.with {
            budget.customer = customer
            name = ' budget'
            amount = 100.00
            warningPercentage = 0.7
        }

        when:
        1 * budgetService.budgetRepository.findByIdAndDateDeletedIsNull(_ as Long) >> budget


        def result = budgetService.find(1)

        then:
        assert result == budget
    }

    def "Should not get a budget and throw exception"(){

        when:
        1 * budgetService.budgetRepository.findByIdAndDateDeletedIsNull(_ as Long) >> null

        budgetService.find(1)

        then:
        InstanceNotFoundException e = thrown()
        e.message == 'budget.notFound'
    }

    def "Should get a budget dto with analysis and transactions not exceeds the default warning percentage"(){
        given:
        def thisMonthSecondDay = new Timestamp( Date.from(ZonedDateTime.now().withDayOfMonth(2).toInstant()).getTime())
        def twoMonthsAgo = new Timestamp(Date.from(ZonedDateTime.now().minusMonths(2).toInstant()).getTime())

        def client = new Client()

        def category = new Category()
        category.id = '123'

        Customer customer = new Customer()
        customer.id = 123
        customer.client = client

        Budget budget = new Budget()
        budget.with {
            budget.category = category
            warningPercentage = 0.7
            amount = 1500
            budget.customer= customer
        }

        List transactionChargeNow = [1, "1", "the only one ok", 1000, true, thisMonthSecondDay, category.id, false, 1000  ]
        List transactionChargeNowCategory = [1, "1", "cleaned", 1000, true, thisMonthSecondDay, 'random', false, 1000  ]

        List transactionChargeTwoMonths = [2 ,"1", "cleaned", 200, true, twoMonthsAgo, '123', false, 1000  ]

        List transactionIncomeNow = [3, "1", "cleaned", 50000, false, thisMonthSecondDay, category.id, false, 50000  ]

        List transactionIncomeTwoMonths = [4, "1", "cleaned", 10000, false, thisMonthSecondDay, category.id, false, 50000  ]



        when:
        def response = budgetService.findById(1)

        then:
        1 * budgetService.budgetRepository.findByIdAndDateDeletedIsNull(_ as Long) >> budget
        1 * budgetService.securityService.getCurrent() >> client

        1 * budgetService.transactionRepository.findAllByCustomerId(_ as Long) >> [transactionChargeNow, transactionChargeNowCategory, transactionChargeTwoMonths, transactionIncomeNow, transactionIncomeTwoMonths]

        assert  response instanceof BudgetDto
        assert  response.spent == 1000
        assert  response.leftToSpend == 500
        assert  response.status == BudgetDto.StatusEnum.ok

    }

    def 'Should edit an budget '(){
        given:'a budget command request body'
        BudgetUpdateCommand cmd = new BudgetUpdateCommand()
        cmd.with {
            name = "Food budget"
            amount= 1234.56
            categoryId = 123
        }

        Customer customer = new Customer()
        customer.id = 987

        Category category = new Category()
        category.with {
            id = 666
        }

        Budget budget = new Budget()
        budget.with {
            name = 'test name'
            warningPercentage = 0.7
            amount = 100.00
            budget.customer = customer
            budget.category = category
        }

        when:
        1 * budgetService.categoryService.findOne(_ as String)>> category
        1 * budgetService.budgetRepository.save(_  as Budget) >> budget
        1 * budgetService.budgetRepository.findByCustomerAndCategoryAndDateDeletedIsNull(_  as Customer, _ as Category) >> null //todo unique escenario
        1 * budgetService.transactionRepository.findAllByCustomerId(_ as Long) >> []


        def response = budgetService.update(cmd, budget)

        then:
        assert response

    }

    def 'Should edit an budget non unique category'(){
        given:'a budget command request body'
        BudgetUpdateCommand cmd = new BudgetUpdateCommand()
        cmd.with {
            name = "Food budget"
            amount= 1234.56
            categoryId = 123
        }

        Customer customer = new Customer()
        customer.id = 987

        Category category = new Category()
        category.with {
            id = 666
        }

        Budget budget = new Budget()
        budget.with {
            name = 'test name'
            warningPercentage = 0.7
            amount = 100.00
            budget.customer = customer
            budget.category = category
        }

        when:
        1 * budgetService.categoryService.findOne(_ as String)>> category
        0 * budgetService.budgetRepository.save(_  as Budget) >> budget
        1 * budgetService.budgetRepository.findByCustomerAndCategoryAndDateDeletedIsNull(_  as Customer, _ as Category) >> new Budget()
        0 * budgetService.transactionRepository.findAllByCustomerId(_ as Long) >> []


        budgetService.update(cmd, budget)

        then:
        BadRequestException e = thrown()
        e.message == 'budget.category.nonUnique'
    }

    def "Should get all budget" () {
        Budget budget = new Budget()
        Customer customer = new Customer()
        customer.id = 999

        budget.with {
            budget.customer = customer
            name = 'test name'
            warningPercentage = 0.7
            category = new Category()
            amount = 1000
        }

        when:
        1 * budgetService.budgetRepository.findAllByDateDeletedIsNull(_ as Map) >> [budget]
        1 * budgetService.transactionRepository.findAllByCustomerId(_ as Long) >> []

        def response = budgetService.getAll()

        then:
        assert response instanceof  List<BudgetDto>
    }

    def "Should not get all budget" () {
        when:
        1 * budgetService.budgetRepository.findAllByDateDeletedIsNull(_ as Map) >> []
        def response = budgetService.getAll()

        then:
        response instanceof  List<BudgetDto>
        response.isEmpty()
    }

    def "Should get budgets by a cursor " () {
        given:
        def thisMonthSecondDay = new Timestamp( Date.from(ZonedDateTime.now().withDayOfMonth(2).toInstant()).getTime())
        def twoMonthsAgo = new Timestamp(Date.from(ZonedDateTime.now().minusMonths(2).toInstant()).getTime())

        Customer customer = new Customer()
        customer.id = 999

        def category = new Category()
        category.id = '123'

        def client = new Client()
        customer.client = client

        Budget budget = new Budget()
        budget.with {
            budget.customer = customer
            name = 'test name'
            warningPercentage = 0.7
            category = new Category()
        }

        List transactionChargeNow = [1 ,"1", "the only one ok", 1000, true, thisMonthSecondDay, category.id ,false, 1000  ]
        List transactionChargeNowCategory = [1 ,"1", "cleaned", 1000, true, thisMonthSecondDay, 'random' ,false, 1000  ]

        List transactionChargeTwoMonths = [2 ,"1", "cleaned", 200, true, twoMonthsAgo, '123', false, 1000  ]

        List transactionIncomeNow = [3 ,"1", "cleaned", 50000, false, thisMonthSecondDay, category.id  ,false, 50000  ]

        List transactionIncomeTwoMonths = [4 ,"1", "cleaned", 10000, false, thisMonthSecondDay, category.id  ,false, 50000  ]




        when:
        1 * budgetService.customerService.findOne(_ as Long) >> customer
        1 * budgetService.securityService.getCurrent() >> client
        1 * budgetService.transactionRepository.findAllByCustomerId(_ as Long) >> [transactionChargeNow, transactionChargeNowCategory, transactionChargeTwoMonths, transactionIncomeNow, transactionIncomeTwoMonths]
        1 * budgetService.budgetRepository.findAllByCustomerAndIdLessThanEqualAndDateDeletedIsNull(_ as Customer,_ as Long, _ as Map) >> [budget]

        def response = budgetService.findAllByCustomerAndCursor(1L,2L)

        then:
        response instanceof  List<BudgetDto>
    }

}
