package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class ScraperV2TokenDto {
	String event
	String institution
	String state
	ScraperV2TokenDataDto data
	
}
