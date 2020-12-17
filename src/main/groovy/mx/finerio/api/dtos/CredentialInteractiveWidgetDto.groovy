package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

@ToString(includePackage = false, includeNames = true)
class CredentialInteractiveWidgetDto {

  @NotNull
  String id

  @NotNull
  String token

  @NotNull(message = 'credential.widgetId.null')
  String widgetId

}