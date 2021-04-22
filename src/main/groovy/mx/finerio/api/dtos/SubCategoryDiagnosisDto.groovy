package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class SubCategoryDiagnosisDto {
    String categoryId
    BigDecimal amount
    List<String> advices = []
}
