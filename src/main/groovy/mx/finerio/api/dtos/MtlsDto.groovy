package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true, includes = ['filename'])
class MtlsDto {

  String filename
  String secret

}
