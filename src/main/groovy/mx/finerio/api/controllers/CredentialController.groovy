package mx.finerio.api.controllers

import javax.validation.Valid

import mx.finerio.api.dtos.CredentialDto
import mx.finerio.api.dtos.CredentialWidgetDto
import mx.finerio.api.dtos.CredentialInteractiveDto
import mx.finerio.api.dtos.CredentialInteractiveWidgetDto
import mx.finerio.api.dtos.CredentialUpdateDto
import mx.finerio.api.services.CredentialErrorService
import mx.finerio.api.services.CredentialService
import mx.finerio.api.services.CredentialWidgetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CredentialController {

  @Autowired
  CredentialErrorService credentialErrorService

  @Autowired
  CredentialService credentialService

  @Autowired
  CredentialWidgetService credentialWidgetService

  @PostMapping('/credentials')
  ResponseEntity create( @RequestBody @Valid CredentialDto credentialDto ) {
  
    def instance = credentialService.create( credentialDto )
    instance = credentialService.getFields( instance )
    new ResponseEntity( instance, HttpStatus.CREATED )
  }

  @GetMapping('/credentials')
  ResponseEntity findAll( @RequestParam Map<String, String> params ) {

    def response = credentialService.findAll( params )
    response.data = response.data.collect {
        credentialService.getFields( it ) }
    new ResponseEntity( response, HttpStatus.OK )

  }

  @GetMapping('/credentials/{id}')
  ResponseEntity findOne( @PathVariable String id ) {

    def instance = credentialService.findOne( id )
    instance = credentialService.getFields( instance )
    new ResponseEntity( instance, HttpStatus.OK )

  }

  @PutMapping('/credentials/{id}')
  ResponseEntity update( @PathVariable String id,
      @RequestBody @Valid CredentialUpdateDto credentialUpdateDto ) {

    if ( credentialUpdateDto.isEmpty() ) {
      credentialService.requestData( id )
    } else {
      credentialService.update( id, credentialUpdateDto )
    }

    new ResponseEntity( HttpStatus.NO_CONTENT )

  }

  @PutMapping('/credentials/{id}/interactive')
  ResponseEntity processInteractive( @PathVariable String id,
      @RequestBody @Valid CredentialInteractiveDto credentialInteractiveDto ) {

    credentialService.processInteractive( id, credentialInteractiveDto )
    ResponseEntity.accepted().build()

  }


  @PutMapping('/p8U55qGnTMLb7HQzZfCjwcQARtVrrgyt8he9fQKz3KgAFPbAwb')
  ResponseEntity processInteractiveWidget( 
      @RequestBody @Valid CredentialInteractiveWidgetDto credentialInteractiveWidgetDto ) {

    credentialService.processInteractiveWidget( credentialInteractiveWidgetDto )
    ResponseEntity.accepted().build()

  }

  @DeleteMapping('/credentials/{id}')
  ResponseEntity delete( @PathVariable String id ) {

    credentialService.delete( id )
    new ResponseEntity( HttpStatus.NO_CONTENT )

  }

  @GetMapping('/credentials/messages/failure')
  ResponseEntity findAllErrors() {

    def response = credentialErrorService.findAll()
    new ResponseEntity( response, HttpStatus.OK )

  }
  
  @PostMapping('/j2GVbQs3kkcBEttuPWZihSFZkoWnIDwQt2zsGRmQZoitHzMllB')
  ResponseEntity createCredentialWidget(
   @RequestBody @Valid CredentialWidgetDto credentialWidgetDto ) {
  
    def instance = credentialWidgetService.create( credentialWidgetDto )    
    new ResponseEntity( instance, HttpStatus.CREATED )

  }

}
