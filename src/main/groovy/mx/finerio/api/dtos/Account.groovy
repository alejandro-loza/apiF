package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class Account {

  BigInteger id
  String name
  String nature
  BigDecimal balance
  String currency_code
  ExtraAccount extra
  BigInteger login_id
  String created_at
  String updated_at

}
