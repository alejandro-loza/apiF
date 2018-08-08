package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class EmailSendDto {

  List to
  TemplateDto template

}
