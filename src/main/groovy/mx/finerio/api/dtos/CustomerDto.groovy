package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

@ToString(includePackage = false, includeNames = true)
class CustomerDto {

  @NotNull(message = 'customer.name.null')
  @Size(min = 1, max = 50, message = 'customer.name.size')
  String name

}
