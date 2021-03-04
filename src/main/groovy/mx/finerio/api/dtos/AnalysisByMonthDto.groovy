package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class AnalysisByMonthDto {

  Date date
  List<AnalysisByCategoryDto> categories = []

}
