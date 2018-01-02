package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class ExtraTransaction {

  String id
  String recordNumber
  String information
  String time
  String postingDate
  String postingTime
  String accountNumber
  BigDecimal originalAmount
  String originalCurrencyCode
  String assetCode
  BigDecimal assetAmount
  String originalCategory
  String originalSubcategory
  String customerCategoryCode
  String customerCategoryName
  boolean possibleDuplicate 
  List tags
  String mcc
  String payee
  String type
  String checkNumber
  BigDecimal units
  String additional
  BigDecimal unitPrice
  BigDecimal accountBalanceSnapshot
  BigDecimal categorizationConfidence

}
