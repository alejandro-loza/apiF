package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

@ToString(includePackage = false, includeNames = true)
class CredentialInteractiveDto {

  @NotNull
  String token

}
