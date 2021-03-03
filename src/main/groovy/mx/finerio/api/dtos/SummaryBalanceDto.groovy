package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class SummaryBalanceDto {

  Date date
  BigDecimal incomes = 0.0
  BigDecimal expenses = 0.0

}
