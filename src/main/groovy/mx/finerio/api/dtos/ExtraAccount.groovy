package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class ExtraAccount {

  String accountName
  String status
  String clientName
  String iban
  String swift
  String cardType
  String accountNumber
  BigDecimal blockedAmount
  BigDecimal aviableAmount
  BigDecimal creditLimit
  BigDecimal interestRate
  String expiryDate
  String openDate
  String currentTime
  String currentDate
  List cards
  BigDecimal units
  BigDecimal unitPrice
  Map transactionsCount
}
