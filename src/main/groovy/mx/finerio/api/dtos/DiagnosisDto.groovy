package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class DiagnosisDto {
    BigDecimal averageIncome
    List<MonthTransactionsDiagnosisDto> data = []
}
