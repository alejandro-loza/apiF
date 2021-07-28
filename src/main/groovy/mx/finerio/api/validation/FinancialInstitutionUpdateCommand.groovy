package mx.finerio.api.validation

import groovy.transform.ToString

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ToString(includeNames = true, includePackage = false)
class FinancialInstitutionUpdateCommand extends ValidationCommand  {

    String code
    String internalCode
    String description
    String name
    String status
    String institutionType
    String country
    String provider
}