package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class ApiListDto {

  List data = []
  Integer nextCursor

}
