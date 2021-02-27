package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class CredentialRangeDto {

  String startDate
  String endDate

}
