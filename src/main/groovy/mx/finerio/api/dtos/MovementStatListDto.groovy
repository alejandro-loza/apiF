package mx.finerio.api.dtos


import java.util.Date
import groovy.transform.ToString
import mx.finerio.api.domain.Category
import mx.finerio.api.domain.User
import mx.finerio.api.domain.Movement.Type

@ToString(includePackage = false, includeNames = true)
class MovementStatListDto {
	User user
	Category category
	Type type
	BigDecimal greaterAmount
	BigDecimal lessAmount
	Date fromDate
	Date toDate
	Boolean groupedByCategory=false
}
