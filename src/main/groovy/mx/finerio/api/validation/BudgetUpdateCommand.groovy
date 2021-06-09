package mx.finerio.api.validation

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class BudgetUpdateCommand {

    Long customerId
    String  categoryId
    String name
    BigDecimal amount
    BigDecimal warningPercentage
}
