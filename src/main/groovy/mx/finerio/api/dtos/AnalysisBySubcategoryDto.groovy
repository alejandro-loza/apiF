package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class AnalysisBySubcategoryDto {

  String categoryId
  BigDecimal average = 0.0
  Integer quantity = 0
  BigDecimal amount = 0.0
  List<AnalysisByTransactionDto> transactions = []

}
