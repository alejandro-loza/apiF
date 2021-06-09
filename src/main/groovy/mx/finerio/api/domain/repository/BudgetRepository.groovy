package mx.finerio.api.domain.repository

import mx.finerio.api.domain.Budget
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface BudgetRepository extends JpaRepository<Budget, Long>, JpaSpecificationExecutor {
    Budget save(Budget budget)
    Budget getById(Long id)
    Budget findByCustomerAndCategoryAndDateDeletedIsNull(Customer customer, Category category)
    List<Budget> findAllByDateDeletedIsNull(Map args)
    List<Budget> findAllByCustomerAndDateDeletedIsNullOrderByIdDesc(Customer customer)
    Budget findByCategoryAndDateDeletedIsNull(Category category)
    List<Budget> findAllByCustomerAndIdLessThanEqualAndDateDeletedIsNull(Customer customer, Long id, Map args)
    List<Budget> findAll()
    Budget findByIdAndDateDeletedIsNull(Long id)
    void delete(Serializable id)
}
