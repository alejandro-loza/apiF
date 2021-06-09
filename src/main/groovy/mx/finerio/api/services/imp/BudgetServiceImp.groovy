package mx.finerio.api.services.imp

import mx.finerio.api.domain.Budget
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Category
import mx.finerio.api.domain.repository.BudgetRepository
import mx.finerio.api.dtos.ApiTransactionDto
import mx.finerio.api.dtos.pfm.BudgetDto
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.services.AccountService
import mx.finerio.api.services.BudgetService
import mx.finerio.api.services.CategoryService
import mx.finerio.api.services.CredentialService
import mx.finerio.api.services.CustomerService
import mx.finerio.api.services.InsightsService
import mx.finerio.api.services.SecurityService
import mx.finerio.api.validation.BudgetCreateCommand
import mx.finerio.api.validation.BudgetUpdateCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional
import java.time.ZonedDateTime

@Service
class BudgetServiceImp extends InsightsService implements BudgetService {

    public static final BigDecimal DEFAULT_WARNING_PERCENTAGE = 0.7
    public static final int MAX_ROWS = 101
    public static final Date THIS_MONTH_FIRST_DAY = Date.from(ZonedDateTime.now().withDayOfMonth(1).toInstant())
    public static final boolean EXPENSE = true

    @Autowired
    BudgetRepository budgetRepository

    @Autowired
    CustomerService customerService

    @Autowired
    CategoryService categoryService

    @Autowired
    SecurityService securityService

    @Autowired
    AccountService accountService

    @Autowired
    CredentialService credentialService

    @Override
    BudgetDto findById(Long id)  throws Exception {
        Budget budget = find(id)
        verifyLoggedClient(budget?.customer?.client)
        crateBudgetDtoWithAnalysis(budget)
    }

    @Override
    @Transactional
    Budget create(BudgetCreateCommand cmd) {
        Budget budget = new Budget()
        Customer customer = customerService.findOne(cmd.customerId)
        budget.with {
            budget.customer = customer
            name = cmd.name
            amount = cmd.amount
            warningPercentage = cmd.warningPercentage ?: DEFAULT_WARNING_PERCENTAGE
            category = findCategoryToSet(cmd.categoryId, customer)
            dateCreated = new Date()
            lastUpdated = new Date()
        }
        budgetRepository.save(budget)
    }

    @Override
    Budget find(Long id) throws InstanceNotFoundException{
        Optional.ofNullable(budgetRepository.findByIdAndDateDeletedIsNull(id))
                .orElseThrow({ -> new InstanceNotFoundException('budget.notFound') })
    }

    @Override
    BudgetDto update(BudgetUpdateCommand cmd, Budget budget){
        verifyLoggedClient(budget?.customer?.client)
        Customer customerToSet = cmd.customerId ? customerService.findOne(cmd.customerId, ) : budget.customer
        budget.with {
            customer = customerToSet
            name = cmd.name ?: budget.name
            amount = cmd.amount ?: budget.amount
            warningPercentage = cmd.warningPercentage ?: budget.warningPercentage
            category = cmd.categoryId ? findCategoryToSet(cmd.categoryId, customer) : budget.category
            lastUpdated = new Date()
        }
        crateBudgetDtoWithAnalysis(budgetRepository.save(budget))
    }

    @Override
    void delete(Long id){
        Budget budget = find(id)
        verifyLoggedClient(budget?.customer?.client)
        budget.dateDeleted = new Date()
        budgetRepository.save(budget)
    }

    @Override
    List<BudgetDto> getAll() {
        budgetRepository
                .findAllByDateDeletedIsNull([max: MAX_ROWS, sort: 'id', order: 'desc'])
                .collect{crateBudgetDtoWithAnalysis(it)}
    }

    @Override
    List<BudgetDto> findAllByCustomerAndCursor(Long customerId, Long cursor) {
        Customer customer = customerService.findOne(customerId)
        verifyLoggedClient(customer.client)
        List<Budget> budgets = budgetRepository
                .findAllByCustomerAndIdLessThanEqualAndDateDeletedIsNull(
                        customer, cursor, [max: MAX_ROWS, sort: 'id', order: 'desc'])

        generateBudgetsDtos(customer, budgets)
    }

    @Override
    Budget findByCategory(Category category) {
        budgetRepository.findByCategoryAndDateDeletedIsNull(category)
    }

    @Override
    List<BudgetDto> findAllByCustomerId(Long userId) {
        findAllByCustomer(customerService.findOne(userId))//todo inspect it
    }

    @Override
    List<BudgetDto> findAllByCustomer(Customer customer) {
        verifyLoggedClient(customer.client)
        List<Budget> budgets = budgetRepository
           .findAllByCustomerAndDateDeletedIsNullOrderByIdDesc(customer)
        if(!budgets.isEmpty()){
           return generateBudgetsDtos(customer, budgets)
        }
        return []
    }

    @Override
    Budget findByCustomerAndCategory(Customer customer, Category category){
        verifyLoggedClient(customer.client)
        budgetRepository.findByCustomerAndCategoryAndDateDeletedIsNull(customer, category)
    }

    @Override
    BudgetDto crateBudgetDtoWithAnalysis(Budget budget) {
        List<ApiTransactionDto> thisMonthFilter = getTransactions(budget.customer.id).findAll {ApiTransactionDto apiTransactionDto ->
            apiTransactionDto.categoryId == budget?.category?.id &&
            apiTransactionDto.date >= THIS_MONTH_FIRST_DAY &&
            apiTransactionDto.isCharge == EXPENSE
        }
        return generateBudgetDto(budget, thisMonthFilter)
    }

    private List<BudgetDto> generateBudgetsDtos(Customer customer, List<Budget> budgets) {

        def categoryTransactions = thisMonthCategoryTransactions(getThisMonthCustomerAccountsExpenses(customer))

        List<BudgetDto> categoryBudgets = budgets.findAll { it.category }.collect { Budget budget ->
            generateBudgetDto(budget, categoryTransactions[budget.category.id])
        }
        categoryBudgets
    }

    private void verifyLoggedClient(Client client) {

        if (client !=  securityService.getCurrent()) {
            throw new InstanceNotFoundException('account.notFound')
        }
    }

    private Category findCategoryToSet(String categoryId, Customer customer) {
        Category categoryToSet = categoryService.findOne(categoryId)
        if (categoryToSet
                && this.findByCustomerAndCategory(customer, categoryToSet)) {
            throw new BadRequestException('budget.category.nonUnique')
        }
        categoryToSet
    }

    private static BudgetDto.StatusEnum calculateStatus(Budget budget, float spend) {
        def limit = budget.amount * budget.warningPercentage
        if(spend < limit ){
            return BudgetDto.StatusEnum.ok
        }
        if(spend >= limit && spend < budget.amount){
            return BudgetDto.StatusEnum.warning
        }
        else{
            return BudgetDto.StatusEnum.danger
        }
    }

    private List<ApiTransactionDto> getThisMonthCustomerAccountsExpenses(Customer customer) {
        getTransactions(customer.id).findAll {ApiTransactionDto apiTransactionDto ->
                    apiTransactionDto.date >= THIS_MONTH_FIRST_DAY &&
                    apiTransactionDto.isCharge == EXPENSE
        }
    }


    private static Map<String, List<ApiTransactionDto>> thisMonthCategoryTransactions(List<ApiTransactionDto> thisMonthTransactions) {
        thisMonthTransactions.findAll {ApiTransactionDto apiTransactionDto-> apiTransactionDto.categoryId }
                .groupBy { it.categoryId }
    }


    private static BudgetDto generateBudgetDto(Budget budget, List<ApiTransactionDto> transactions) {
        BudgetDto budgetDto = new BudgetDto()
        budgetDto.with {
            id = budget.id
            categoryId =  budget.category.id
            name = budget.name
            amount = budget.amount
            warningPercentage = budget.warningPercentage
            spent = transactions ? transactions*.amount.sum() as float : 0
            leftToSpend =  amount - spent > 0 ? amount - spent : 0
            status = calculateStatus(budget, spent)
            dateCreated = budget.dateCreated
            lastUpdated = budget.lastUpdated
        }
        budgetDto
    }




}
