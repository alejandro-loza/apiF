package mx.finerio.api.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

import mx.finerio.api.domain.PasswordChangeRequest
import mx.finerio.api.domain.User
import mx.finerio.api.domain.repository.PasswordChangeRequestRepository
import mx.finerio.api.domain.repository.UserRepository
import mx.finerio.api.dtos.ErrorDto
import mx.finerio.api.dtos.NewPasswordDto
import mx.finerio.api.exceptions.BadImplementationException

@Service
class ForgotPasswordService {
	
	@Autowired
	UserRepository userRepository
	
	@Autowired
	MessageSource messageSource
	
	@Autowired
	PasswordChangeRequestRepository passwordChangeRequestRepository
	
	@Autowired
	PasswordEncoder passwordEncoder
	
	@Value('${mail.service.template.forgot.password.name')
	String templateName
	
	@Value('${finerio.email.front.url}')
	String frontEndURL
	
	@Autowired
	EmailRestService emailRestService

	ErrorDto createForgotPasswordToken( String emaildId ) throws Exception {
		
		if ( !emaildId ) {
			throw new BadImplementationException(
				'forgotPasswordService.createForgotPasswordToken.emaildId.null' )
		  }
		
		
		User user=userRepository.findOneByUsername(emaildId)
		ErrorDto errorDto=new ErrorDto()
		
		if( !user ) {
			errorDto.code="forgot_password_user_notfound"
		}else if( !user.enabled ) {
			errorDto.code="forgot_password_user_notfound"
		}else {
			createAndInsertToken(user)
			errorDto.code="forgot_password_user_found"
		}
		errorDto
	}

		
	ErrorDto setNewPassword( NewPasswordDto newPasswordDto ) throws Exception {	
		
		if ( !newPasswordDto ) {
			throw new BadImplementationException(
				'forgotPasswordService.setNewPassword.newPasswordDto.null' )
		  }
		
		ErrorDto errorDto=new ErrorDto()
		
		if(!newPasswordDto.password.equals(newPasswordDto.passwordConfirmation)) {
			errorDto.code='forgotPasswordService_password_noMatch'
		}else {
			PasswordChangeRequest pcr=getPasswordChangeRequestAnValidateToken(newPasswordDto.token)
			if(!pcr){
				errorDto.code='forgotPasswordService_invalid_token'
			}else {
				User user=userRepository.findOneByUsername(newPasswordDto.email)
				user.password=passwordEncoder.encode(newPasswordDto.password)
				userRepository.save(user)
				pcr.valid=false
				passwordChangeRequestRepository.save(pcr)	
				errorDto.code='forgotPasswordService_setNewPassword_success'
			}
		}
		errorDto
	}
	
	Map getEmailAndvalidateToken( String token ) throws Exception {
		
		if ( !token ) {
			throw new BadImplementationException(
				'forgotPasswordService.getEmailAndvalidateToken.token.null' )
		  }
		
		PasswordChangeRequest pcr=getPasswordChangeRequestByEmail(token)
		if(!pcr)
			return [email:null]
			
		[email:getEmailAfterValidation(pcr)]
	}
	
	
	private void createAndInsertToken(User user) {
		
		List<PasswordChangeRequest> requestList=passwordChangeRequestRepository.findByUserAndValidTrue(user)
		
		if(!requestList.empty) {
			requestList.collect {
				it.valid=false
				passwordChangeRequestRepository.save(it)
				}
		}
	
		PasswordChangeRequest pcr=new PasswordChangeRequest()
		pcr.token=UUID.randomUUID()
		pcr.requestDate= new Date()
		pcr.user=user
		pcr.valid=true;
		passwordChangeRequestRepository.save(pcr)
		emailRestService.send(user.username, templateName, ['uniquelink':"${frontEndURL}/${pcr.token}"])
	}
	
	private PasswordChangeRequest getPasswordChangeRequestAnValidateToken(String token ) {
		PasswordChangeRequest pcr=getPasswordChangeRequestByEmail(token)
		if(!pcr)
			return null
		def email=getEmailAfterValidation(pcr)
		 (email)?pcr:null;
	}
	
	private String getEmailAfterValidation(PasswordChangeRequest pcr) {
		def now = new Date()
		def diffInMillis = now.time - pcr.requestDate.time
		if(pcr && pcr.valid && diffInMillis <= 86400000L ) {
			return pcr.user.username
		}
		null
	}
	private PasswordChangeRequest getPasswordChangeRequestByEmail(String token) {
		PasswordChangeRequest pcr=passwordChangeRequestRepository.findOneByTokenAndValidTrue(token)
		pcr
	}
	
	@Bean
	 BCryptPasswordEncoder passwordEncoder() {
		 new BCryptPasswordEncoder(10);
	}
}

