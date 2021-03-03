package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class SummaryByMonthDto {

  Date date
  BigDecimal amount = 0.0
  List<SummaryByCategoryDto> categories = []

}
