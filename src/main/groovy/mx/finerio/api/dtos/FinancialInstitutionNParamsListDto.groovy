package mx.finerio.api.dtos

import groovy.transform.ToString
import mx.finerio.api.domain.Country
import mx.finerio.api.domain.FinancialInstitution

@ToString(includePackage = false, includeNames = true)
class FinancialInstitutionNParamsListDto extends ListDto {

    List<Country> countries
    List<FinancialInstitution.InstitutionType> types

}

