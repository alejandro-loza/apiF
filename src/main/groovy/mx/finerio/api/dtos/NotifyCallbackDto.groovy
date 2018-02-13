package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class NotifyCallbackDto {

  NotifyCallbackData data
  Map meta

}
