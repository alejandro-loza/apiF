package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class TransactionData {

  String credential_id
  String account_id
  List<Transaction> transactions

}

