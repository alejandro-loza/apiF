package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class CreateExtractionDto {

	String taxpayer
	String extractor
	CreateExtractionOptionsDto options
	String credentialId

}
