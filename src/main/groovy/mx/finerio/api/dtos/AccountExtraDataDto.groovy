package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class AccountExtraDataDto {

  String accountId
  String name
  String value

}
