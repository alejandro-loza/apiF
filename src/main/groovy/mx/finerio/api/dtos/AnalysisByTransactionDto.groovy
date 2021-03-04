package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class AnalysisByTransactionDto {

  String description
  BigDecimal average = 0.0
  Integer quantity = 0
  BigDecimal amount = 0.0

}
