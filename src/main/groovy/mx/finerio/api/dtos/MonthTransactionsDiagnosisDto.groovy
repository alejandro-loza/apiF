package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class MonthTransactionsDiagnosisDto {
    String date
    List<CategoryDiagnosisDto> categories = []
}
