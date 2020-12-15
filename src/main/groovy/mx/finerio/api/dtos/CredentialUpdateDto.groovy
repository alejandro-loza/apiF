package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

import mx.finerio.api.domain.Client

@ToString(includePackage = false, includeNames = true, excludes = [ 'password', 'securityCode' ] )
class CredentialUpdateDto {

  @Size(min = 1, max = 50, message = 'credential.password.size')
  String password

  @Size(min = 1, max = 10, message = 'credential.securityCode.size')
  String securityCode

  Boolean automaticFetching

  Client client

  boolean isEmpty() {
    !password && !securityCode && automaticFetching == null
  }

}
