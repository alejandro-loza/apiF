package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class FailureCallbackData {

  String credential_id
  String error_message

}
