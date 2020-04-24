package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class CreateAllAccountExtraDataDto {

  String accountId
  Map extraData
  String prefix

}
