package mx.finerio.api.dtos

import groovy.transform.ToString
import mx.finerio.api.domain.FinancialInstitution

@ToString(includePackage = false, includeNames = true)
class FinancialInstitutionDto {

  Long id
  Long version
  String code
  String description
  String name
  String status
  Date dateCreated

  FinancialInstitutionDto(FinancialInstitution institution) {
    this.id = institution.id
    this.version = institution.version
    this.code = institution.code
    this.description = institution.description
    this.name = institution.name
    this.status = institution.status
    this.dateCreated = institution.dateCreated
  }


}
