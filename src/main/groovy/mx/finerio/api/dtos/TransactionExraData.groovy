package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class TransactionExtraData {

  String transaction_Id
  String balance

}
