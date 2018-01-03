package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class ExtraTransaction {

  String id
  String record_number
  String information
  String time
  String posting_date
  String posting_time
  String account_number
  BigDecimal original_amount
  String original_currency_code
  String asset_code
  BigDecimal asset_amount
  String original_category
  String original_subcategory
  String customer_category_code
  String customer_category_name
  boolean possible_duplicate 
  List tags
  String mcc
  String payee
  String type
  String check_number
  BigDecimal units
  String additional
  BigDecimal unit_price
  BigDecimal account_balance_snapshot
  BigDecimal categorization_confidence

}
