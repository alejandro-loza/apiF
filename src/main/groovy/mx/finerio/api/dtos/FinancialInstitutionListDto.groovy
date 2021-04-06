package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

import mx.finerio.api.domain.Country

import mx.finerio.api.domain.FinancialInstitution

@ToString(includePackage = false, includeNames = true)
class FinancialInstitutionListDto extends ListDto { 
  Country country
  FinancialInstitution.InstitutionType type

}
