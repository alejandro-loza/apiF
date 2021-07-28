package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

@ToString(includePackage = false, includeNames = true, excludes = [ 'password', 'securityCode' ] )
class CredentialDto extends CredentialRangeDto {

  @NotNull(message = 'credential.username.null')
  @Size(min = 1, max = 50, message = 'credential.username.size')
  String username

  @NotNull(message = 'credential.password.null')
  @Size(min = 1, max = 50, message = 'credential.password.size')
  String password

  @Size(min = 1, max = 50, message = 'credential.securityCode.size')
  String securityCode

  @Size(min = 1, max = 50, message = 'credential.token.size')
  String otp

  @NotNull(message = 'credential.bankId.null')
  Long bankId

  @NotNull(message = 'credential.customerId.null')
  Long customerId

  Boolean automaticFetching = true

  String state

}
