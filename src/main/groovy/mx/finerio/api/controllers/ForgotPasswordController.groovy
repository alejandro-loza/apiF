package mx.finerio.api.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import mx.finerio.api.dtos.NewPasswordDto
import mx.finerio.api.services.ForgotPasswordService

@RestController
class ForgotPasswordController {
	
	
	@Autowired
	ForgotPasswordService forgotPasswordService
		
	@GetMapping('/password/createForgotPasswordToken')
	ResponseEntity createForgotPasswordToken( @RequestParam String email ) {
		def response = forgotPasswordService.createForgotPasswordToken(email)		
		new ResponseEntity( response, HttpStatus.OK )
	}
	
	@GetMapping('/password/getEmailAndValidateToken')
	ResponseEntity getEmailAndValidateToken( @RequestParam String token ) {
		def response = forgotPasswordService.getEmailAndvalidateToken(token)
		new ResponseEntity( response, HttpStatus.OK )
	}
	
	@PostMapping('/password/setNewPassword')
	ResponseEntity setNewPassword( @RequestBody NewPasswordDto newPasswordDto ) {
		def response = forgotPasswordService.setNewPassword(newPasswordDto)
		new ResponseEntity( response, HttpStatus.CREATED )
		

	}  
}
