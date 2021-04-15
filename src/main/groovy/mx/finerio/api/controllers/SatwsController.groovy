package mx.finerio.api.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.http.ResponseEntity
import mx.finerio.api.dtos.SatwsEventDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import mx.finerio.api.services.SatwsService

@RestController
class SatwsController {

  @Autowired
  SatwsService satwsService

  @PostMapping( '/FM73Eptuzq24683NNg37GeCHHme4KdaUa2d46S89FCp9MCTnfw' )
  ResponseEntity events( @RequestBody SatwsEventDto satwsEventDto ) {
    satwsService.processEvent( satwsEventDto )    
    ResponseEntity.ok().build()
  }

}
