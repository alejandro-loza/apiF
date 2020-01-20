package mx.finerio.api.dtos

import groovy.transform.ToString
import javax.validation.constraints.*

@ToString(includePackage = false, includeNames = true, excludes = [ 'password' ] )
class UpdateClientDto {

  @Size(min = 8, max = 50, message = 'client.password.size')
  String password

  Boolean categorize

  boolean isEmpty() {
    !password && ( categorize == null )
  }

}
