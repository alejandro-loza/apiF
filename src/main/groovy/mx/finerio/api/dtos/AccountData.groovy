package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class AccountData {

  String credential_id
  String name
  String nature
  BigDecimal balance
  Map extra_data

}