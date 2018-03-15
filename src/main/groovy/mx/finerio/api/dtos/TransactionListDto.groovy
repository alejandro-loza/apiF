package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

import mx.finerio.api.domain.Account

@ToString(includePackage = false, includeNames = true)
class TransactionListDto extends ListDto {

  Long id
  Account account

}
