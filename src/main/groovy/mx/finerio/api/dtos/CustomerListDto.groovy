package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

import mx.finerio.api.domain.Client

@ToString(includePackage = false, includeNames = true)
class CustomerListDto extends ListDto {

  Long cursor
  Client client
  String word

}
