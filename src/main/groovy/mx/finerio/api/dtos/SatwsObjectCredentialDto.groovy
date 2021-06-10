package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class SatwsObjectCredentialDto {

  SatwObjectMetadataDto metadata
  String status
  String rfc
    
}
