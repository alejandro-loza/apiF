package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

@ToString(includePackage = false, includeNames = true )
class CreditDetailsDto {

  BigDecimal annual_porcentage_rate

  @Size(min = 1, max = 50, message = 'creditDetails.card_number.size')
  String card_number

  Date closing_date

  BigDecimal credit_limit

  Date due_date

  Date last_closing_date

  BigDecimal minimum_payment

  BigDecimal non_interest_payment

  BigDecimal statement_balance

}
