package mx.finerio.api.dtos

import groovy.transform.ToString
import javax.validation.constraints.*

@ToString(includePackage = false, includeNames = true, excludes = [ 'password' ] )
class ClientDto {

  @NotNull(message = 'client.name.null')
  @Size(min = 1, max = 50, message = 'client.name.size')
  String name

  @NotNull(message = 'client.password.null')
  @Size(min = 8, max = 50, message = 'client.password.size')
  String password

}
