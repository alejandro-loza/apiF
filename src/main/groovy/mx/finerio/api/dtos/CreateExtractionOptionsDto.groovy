package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class CreateExtractionOptionsDto {

	Character[] types
  CreateExtractionPeriodDto period

}
