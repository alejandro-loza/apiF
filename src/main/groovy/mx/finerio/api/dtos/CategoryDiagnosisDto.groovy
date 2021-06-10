package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class CategoryDiagnosisDto {
    String categoryId
    BigDecimal spent
    BigDecimal average
    BigDecimal others
    BigDecimal suggested
    List<SubCategoryDiagnosisDto> subcategories = []
}
