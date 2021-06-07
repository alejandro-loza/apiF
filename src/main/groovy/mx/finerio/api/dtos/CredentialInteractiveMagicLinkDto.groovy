package mx.finerio.api.dtos

import groovy.transform.ToString
import javax.validation.constraints.NotNull

@ToString(includePackage = false, includeNames = true)
class CredentialInteractiveMagicLinkDto {
    @NotNull
    String otp
}
