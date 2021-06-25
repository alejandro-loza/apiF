package mx.finerio.api.validation

import groovy.transform.ToString
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ToString(includeNames = true, includePackage = false)
class FinancialInstitutionCreateCommand extends ValidationCommand  {

    @NotNull(message= 'financialInstitution.code.null')
    @Size(min = 1, max = 50, message = 'financialInstitution.code.size')
    String code

    @NotNull(message= 'financialInstitution.internalCode.null')
    @Size(min = 1, max = 50, message = 'financialInstitution.internalCode.size')
    String internalCode

    @NotNull(message= 'financialInstitution.description.null')
    @Size(min = 1, max = 50, message = 'financialInstitution.description.size')
    String description

    @NotNull(message= 'financialInstitution.name.null')
    @Size(min = 1, max = 50, message = 'financialInstitution.name.size')
    String name

    @NotNull(message= 'financialInstitution.status.null')
    @Size(min = 1, max = 50, message = 'financialInstitution.status.size')
    String status

    @NotNull(message= 'financialInstitution.institutionType.null')
    @Size(min = 1, max = 50, message = 'financialInstitution.institutionType..size')
    String institutionType

    @NotNull(message= 'financialInstitution.country.null')
    @Size(min = 1, max = 50, message = 'financialInstitution.country.size')
    String country

    @NotNull(message= 'financialInstitution.institutionType.null')
    @Size(min = 1, max = 50, message = 'financialInstitution.institutionType.size')
    String provider

    @NotNull(message= 'financialInstitution.customerId.null')
    Long  customerId

}