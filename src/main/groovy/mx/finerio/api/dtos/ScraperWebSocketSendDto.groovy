package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class ScraperWebSocketSendDto {

  String id
  String message
  Boolean destroyPreviousSession = false

}
