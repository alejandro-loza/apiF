package mx.finerio.api.controllers

import mx.finerio.api.services.MagicLinkService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping


class MagicLinkController {

    @Autowired
    MagicLinkService customerEmailService

    @PostMapping('/magicLink/customers/{id}/email/magicLink')
    ResponseEntity sendMagicLink(@PathVariable Long customerId ) {
        def mexico='2abe9160-6451-44fd-8330-f0f3a25fd3d4'
        customerEmailService.sendMagicLink( customerId, mexico )
        new ResponseEntity( HttpStatus.NO_CONTENT )
    }

    @GetMapping('/magicLink/{customerLinkId}/banks')
    ResponseEntity findBanksByCustomerLinkId( Long customerLinkId ) {
        def response = customerEmailService
                .findBanksByCustomerLinkId( customerLinkId )
        new ResponseEntity( response, HttpStatus.OK )
    }
}
