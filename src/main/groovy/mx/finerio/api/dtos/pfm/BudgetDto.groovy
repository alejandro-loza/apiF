package mx.finerio.api.dtos.pfm

import groovy.transform.ToString
import mx.finerio.api.domain.Budget


@ToString(includeNames = true, includePackage = false)
class BudgetDto {
    enum StatusEnum {
        ok, warning, danger
    }

    Long id
    String categoryId
    String name
    BigDecimal amount
    float warningPercentage
    BigDecimal spent
    BigDecimal leftToSpend
    StatusEnum status
    Date dateCreated
    Date lastUpdated

    BudgetDto(){}

    BudgetDto(Budget budget) {
        this.id = budget.id
        this.categoryId = budget.category.id
        this.name = budget.name
        this.amount = budget.amount
        this.dateCreated = budget.dateCreated
        this.lastUpdated = budget.lastUpdated
         this.warningPercentage = warningPercentage
    }

}
