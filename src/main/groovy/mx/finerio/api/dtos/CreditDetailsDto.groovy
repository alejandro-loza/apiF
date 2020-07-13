package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

@ToString(includePackage = false, includeNames = true )
class CreditDetailsDto {

  BigDecimal annual_porcentage_rate

  @Size(min = 1, max = 50, message = 'creditDetails.card_number.size')
  String card_number

  String closing_date

  BigDecimal credit_limit

  String due_date

  String last_closing_date

  BigDecimal minimum_payment

  BigDecimal non_interest_payment

  BigDecimal statement_balance

  BigDecimal available_balance

}
