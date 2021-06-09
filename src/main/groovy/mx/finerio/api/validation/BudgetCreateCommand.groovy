package mx.finerio.api.validation

import groovy.transform.CompileStatic
import groovy.transform.ToString
import javax.annotation.Nullable
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ToString(includeNames = true, includePackage = false)
@CompileStatic
class BudgetCreateCommand {

    @NotNull(message= 'category.id.null')
    Long customerId

    @NotNull(message= 'category.null')
    @Size(min = 1, max = 50, message = 'credential.username.size')
    String  categoryId

    @NotNull(message= 'budget.name.null')
    @Size(min = 1, max = 50, message = 'credential.username.size')
    String name

    @NotNull(message= 'budget.amount.null')
    BigDecimal amount

    @Nullable
    @DecimalMin(value = "0.1")
    @DecimalMax(value = "1.0")
    BigDecimal warningPercentage

}
