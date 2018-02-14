package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class AccountDto {

  AccountData data
  Map meta

}
