package mx.finerio.api.controllers

import java.util.Map

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import mx.finerio.api.services.MovementStatService
import mx.finerio.api.services.TransactionService
import org.springframework.http.HttpStatus

	@RestController
	class MovementStatController {
	
	  @Autowired
	  MovementStatService movementStatService
	
	  @GetMapping('/movementsStat')
	  ResponseEntity findAll( @RequestParam Map<String, String> params ) {
	
		def response = movementStatService.findAll( params )
		response.data = response.data.collect {
			movementStatService.getFields( it ) }
		new ResponseEntity( response, HttpStatus.OK )
	  }
	
	}
	
