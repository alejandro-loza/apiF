package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class Request {

  String account_id
  String credential_id
  List<Transaction> transactions

}

