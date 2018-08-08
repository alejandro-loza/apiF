package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class NewPasswordDto {
	String email
	String password
	String passwordConfirmation
	String token
}
