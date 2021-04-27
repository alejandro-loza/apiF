package mx.finerio.api.domain.repository

import mx.finerio.api.domain.Category
import mx.finerio.api.domain.SuggestedExpenses
import mx.finerio.api.domain.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SuggestedExpensesRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor {
  @Query(value = "from SuggestedExpenses t where t.category = :category AND t.incomeFrom >= :income AND t.incomeTo <= :income")
  SuggestedExpenses findByCategoryAndIncome(@Param("category")Category category, @Param("income")BigDecimal income )
}
