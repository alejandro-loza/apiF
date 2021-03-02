package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class ApiTransactionDto {

  Long id
  String description
  String cleanedDescription
  BigDecimal amount
  Boolean isCharge
  Date date
  String categoryId
  Boolean duplicated
  BigDecimal balance
  String currency = 'MXN'

}
