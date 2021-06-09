package mx.finerio.api.services

import mx.finerio.api.domain.Budget
import mx.finerio.api.domain.Category
import mx.finerio.api.domain.Customer
import mx.finerio.api.dtos.pfm.BudgetDto
import mx.finerio.api.validation.BudgetCreateCommand
import mx.finerio.api.validation.BudgetUpdateCommand

interface BudgetService {

    BudgetDto findById(Long id)
    Budget create(BudgetCreateCommand cmd)
    Budget find(Long id)
    BudgetDto update(BudgetUpdateCommand cmd, Budget budget)
    void delete(Long id)
    List<BudgetDto> getAll()
    List<BudgetDto> findAllByCustomerAndCursor(Long customerId, Long cursor)
    List<BudgetDto> findAllByCustomerId(Long customerId)
    List<BudgetDto> findAllByCustomer(Customer customer)
    Budget findByCustomerAndCategory(Customer customer, Category category)
    Budget findByCategory(Category category)
    BudgetDto crateBudgetDtoWithAnalysis(Budget budget)

}