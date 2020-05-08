package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class DuplicatedTransactionDto {

  String description
  BigDecimal amount
  boolean deposit
  String transactionId
   
}
