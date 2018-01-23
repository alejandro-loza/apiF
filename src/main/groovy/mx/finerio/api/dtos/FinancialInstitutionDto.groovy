package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class FinancialInstitutionDto {

  Long id
  Long version
  String code
  String description
  String name
  String status

}
