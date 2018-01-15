package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class Transaction {

  BigInteger id
  String mode
  String status
  String made_on
  BigDecimal amount
  String currency_code
  String description
  String category
  boolean duplicated
  ExtraTransaction extra
  String account_id
  String created_at
  String updated_at

}

