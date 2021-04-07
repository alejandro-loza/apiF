package mx.finerio.api.dtos.email

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class EmailTemplateDto {

  String name
  Map params

}
