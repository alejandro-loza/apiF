package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class Transaction {

  BigInteger id
  String mode
  String status
  Date madeOn
  BigDecimal amount
  String currencyCode
  String description
  String category
  boolean duplicated
  ExtraTransaction extra
  BigInteger accountId
  String createdAt
  String updatedAt

}

