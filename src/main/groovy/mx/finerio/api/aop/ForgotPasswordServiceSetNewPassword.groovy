package mx.finerio.api.aop

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import mx.finerio.api.dtos.ErrorDto
import mx.finerio.api.dtos.NewPasswordDto

@Component
@Aspect
class ForgotPasswordServiceSetNewPassword {

  final static Logger log = LoggerFactory.getLogger(
	  'mx.finerio.api.aop.ForgotPasswordServiceSetNewPassword' )

  @Pointcut(
	value='execution(mx.finerio.api.dtos.ErrorDto mx.finerio.api.services.ForgotPasswordService.setNewPassword(..)) && bean(forgotPasswordService) && args(newPasswordDto)',
	argNames='newPasswordDto'
  )
  public void setNewPassword( NewPasswordDto newPasswordDto ) {}

  @Before('setNewPassword(newPasswordDto)')
  void before( NewPasswordDto newPasswordDto ) {
	log.info( "<< newPasswordDto: {}", newPasswordDto )
  }

  @AfterReturning(
	pointcut='setNewPassword(mx.finerio.api.dtos.NewPasswordDto)',
	returning='response'
  )
  void afterReturning( ErrorDto response ) {
	log.info( '>> response: {}', response )
  }

  @AfterThrowing(
	pointcut='setNewPassword(mx.finerio.api.dtos.NewPasswordDto)',
	throwing='e'
  )
  void afterThrowing( Exception e ) {
	log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}

