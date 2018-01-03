package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class Account {

  BigInteger id
  String name
  String nature
  BigDecimal balance
  String currencyCode
  ExtraAccount extra
  BigInteger loginId
  String createdAt
  String updatedAt

}
