package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

@ToString(includePackage = false, includeNames = true)
class CallbackUpdateDto {

  @NotNull(message = 'callback.url.null')
  @Size(min = 1, max = 200, message = 'callback.url.size')
  String url

}
