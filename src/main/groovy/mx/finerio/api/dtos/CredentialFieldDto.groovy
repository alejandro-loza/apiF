package mx.finerio.api.dtos

import groovy.transform.ToString
@ToString(includeNames = true, includePackage = false, excludes = 'value')
class CredentialFieldDto {
    String name
    String value
}
