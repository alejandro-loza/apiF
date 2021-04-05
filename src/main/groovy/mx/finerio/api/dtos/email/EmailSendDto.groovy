package mx.finerio.api.dtos.email

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class EmailSendDto {

  EmailFromDto from
  List<String> to
  EmailTemplateDto template

}
