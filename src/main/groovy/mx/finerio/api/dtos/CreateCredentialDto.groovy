package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class CreateCredentialDto {

  String bankCode  
  String username
  String password
  String credentialId
  String endDate
  String startDate
  
}