package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.*

import mx.finerio.api.domain.Callback.Nature

@ToString(includePackage = false, includeNames = true)
class CallbackDto {

  @NotNull(message = 'callback.url.null')
  @Size(min = 1, max = 200, message = 'callback.url.size')
  String url

  @NotNull(message = 'callback.nature.null')
  Nature nature

}
