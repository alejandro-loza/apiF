package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

import mx.finerio.api.domain.Customer

@ToString(includePackage = false, includeNames = true)
class CredentialListDto extends ListDto {

  Date dateCreated
  Customer customer

}
