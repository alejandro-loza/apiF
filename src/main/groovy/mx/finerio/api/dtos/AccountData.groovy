package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class AccountData {

  String user_id
  String credential_id
  String name
  String nature
  BigDecimal balance
  Map extra_data
  String id
  CreditDetailsDto credit_card_detail
  Boolean is_credit_card

}
