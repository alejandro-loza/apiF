package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

import mx.finerio.api.domain.Account

@ToString(includePackage = false, includeNames = true)
class MovementListDto extends ListDto {

  Date dateCreated
  Account account

}
