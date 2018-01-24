package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class CredentialDto {

  String id
  String username
  String password
  String securityCode
  String iv
  UserDto user
  FinancialInstitutionDto institution
  String dateCreated
  String lastUpdated

}
