package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class Transaction {

  BigInteger id
  String mode
  String status
  Date madeOn
  BigDecimal amount
  String currency_code
  String description
  String category
  boolean duplicated
  ExtraTransaction extra
  BigInteger account_id
  String created_at
  String updated_at

}

