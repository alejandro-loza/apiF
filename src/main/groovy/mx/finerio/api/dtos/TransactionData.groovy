package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class TransactionData {

  String account_id
  List<Transaction> transactions

}

