package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class SummaryDto {

  List<SummaryByMonthDto> incomes = []
  List<SummaryByMonthDto> expenses = []
  List<SummaryBalanceDto> balances = []

}
