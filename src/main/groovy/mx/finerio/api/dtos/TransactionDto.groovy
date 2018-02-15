package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class TransactionDto {

  TransactionData data
  Map meta

}
