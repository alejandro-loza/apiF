package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class WidgetEventsDto {

  String credentialId
  String accountId
  String accountName
  String message
  String code
  String bankToken

}

