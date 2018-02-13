package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class NotifyCallbackData {

  String credential_id
  String stage

}
