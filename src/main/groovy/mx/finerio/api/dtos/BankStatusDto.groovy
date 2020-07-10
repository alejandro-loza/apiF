package mx.finerio.api.dtos

import groovy.transform.ToString

import javax.validation.constraints.NotNull

@ToString(includePackage = false, includeNames = true)
class BankStatusDto {

  @NotNull(message = 'bankStatus.bankId.null')
  Long bankId

  @NotNull(message = 'bankStatus.status.null')
  String status

  @NotNull(message = 'bankStatus.notifyClients.null')
  Boolean notifyClients

}
