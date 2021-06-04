package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class SatwsObjectDto {

  String id
  String status
  String extractor
  SatwObjectMetadataDto metadata
  SatwsObjectCredentialDto credential
    
}
