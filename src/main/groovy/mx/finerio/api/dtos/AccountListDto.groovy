package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

import mx.finerio.api.domain.Credential

@ToString(includePackage = false, includeNames = true)
class AccountListDto extends ListDto {

  Date dateCreated
  Credential credential

}
