package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class BalanceRowDto {

  ApiTransactionDto transaction
  BigDecimal amountBeforeTransaction
  BigDecimal amountAfterTransaction

}
