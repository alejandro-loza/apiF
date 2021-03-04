package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class BalanceDto {

  BigDecimal lastBalance
  BigDecimal average
  List<BalanceRowDto> history = []

}
