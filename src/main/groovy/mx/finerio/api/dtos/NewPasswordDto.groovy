package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true, excludes="password,passwordConfirmation")
class NewPasswordDto {
	String email
	String password
	String passwordConfirmation
	String token
}
