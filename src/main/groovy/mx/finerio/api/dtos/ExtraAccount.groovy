package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class ExtraAccount {

  String account_name
  String status
  String client_name
  String iban
  String swift
  String card_type
  String account_number
  BigDecimal blocked_amount
  BigDecimal aviable_amount
  BigDecimal credit_limit
  BigDecimal interest_rate
  String expiry_date
  String open_date
  String current_time
  String current_date
  List cards
  BigDecimal units
  BigDecimal unit_price
  Map transactions_count
}
