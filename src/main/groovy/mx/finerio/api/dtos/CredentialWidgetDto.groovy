package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

@ToString( includePackage = false, includeNames = true, excludes = [ 'password', 'securityCode' ] )
class CredentialWidgetDto {

  String username
  String password
  String securityCode
  Long bankId
  Long customerId
  String customerName
  String credentialId
  Boolean automaticFetching = true
  String state
  String widgetId

}
