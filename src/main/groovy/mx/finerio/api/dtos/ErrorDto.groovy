package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class ErrorDto {

  String code
  String title
  String detail

}
