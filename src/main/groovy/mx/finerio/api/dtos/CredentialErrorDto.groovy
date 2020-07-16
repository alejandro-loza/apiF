package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class CredentialErrorDto {

  Integer code
  String key
  String description
  String text
 
}
