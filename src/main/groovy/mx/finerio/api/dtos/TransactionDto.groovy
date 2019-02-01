package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class TransactionDto {

  TransactionData data
  Map meta


 static  TransactionDto getInstanceFromCredentialId( String credentialId ){
 	if( !credentialId ){ return null}
 	def data = new TransactionData()
 	data.credential_id = credentialId
 	def dto = new TransactionDto()
 	dto.data=data
  	dto
 }


}
