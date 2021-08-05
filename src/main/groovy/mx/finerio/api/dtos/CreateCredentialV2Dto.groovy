package mx.finerio.api.dtos

import groovy.transform.ToString



@ToString(includeNames = true, includePackage = false)
class CreateCredentialV2Dto {

    Long bankId
    List<CredentialFieldDto> fields

}
