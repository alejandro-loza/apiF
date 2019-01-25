package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class SuccessCallbackDto {

  SuccessCallbackData data
  Map meta

 static  SuccessCallbackDto getInstanceFromCredentialId( String credentialId ){
 	if( !credentialId ){ return null}
 	def data = new SuccessCallbackData()
 	data.credential_id = credentialId
 	def dto = new SuccessCallbackDto()
 	dto.data=data
  	dto
 }

}
