package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class ScraperV2TokenDataDto {
	String field_name
	String value
	String content_type
}
