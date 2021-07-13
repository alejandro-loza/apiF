package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(excludes = 'password', includeNames = true, includePackage = false)
class CreateCredentialSatwsDto {

  String rfc
  String password
  String type
  String credentialId
  Long customerId
   
}