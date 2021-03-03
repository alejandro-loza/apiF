package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class SummaryBySubcategoryDto {

  String categoryId
  BigDecimal amount = 0.0
  List<ApiTransactionDto> transactions = []

}
