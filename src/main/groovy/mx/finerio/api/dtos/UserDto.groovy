package mx.finerio.api.dtos

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class UserDto {

  String id
  String username
  String password
  boolean enabled
  boolean accountNonExpired
  boolean accountNonLocked
  boolean credentialsNonExpired
}
