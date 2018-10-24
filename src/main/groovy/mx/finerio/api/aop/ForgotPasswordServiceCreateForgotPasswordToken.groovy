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

@Component
@Aspect
class ForgotPasswordServiceCreateForgotPasswordToken {

  final static Logger log = LoggerFactory.getLogger(
	  'mx.finerio.api.aop.ForgotPasswordServiceCreateForgotPasswordToken' )

  @Pointcut(
	value='execution(mx.finerio.api.dtos.ErrorDto mx.finerio.api.services.ForgotPasswordService.createForgotPasswordToken(..)) && bean(forgotPasswordService) && args(emaildId)',
	argNames='emaildId'
  )
  public void createForgotPasswordToken( String emaildId ) {}

  @Before('createForgotPasswordToken(emaildId)')
  void before( String emaildId ) {
	log.info( "<< emaildId: {}", emaildId )
  }

  @AfterReturning(
	pointcut='createForgotPasswordToken(java.lang.String)',
	returning='response'
  )
  void afterReturning( ErrorDto response ) {
	log.info( '>> response: {}', response )
  }

  @AfterThrowing(
	pointcut='createForgotPasswordToken(java.lang.String))',
	throwing='e'
  )
  void afterThrowing( Exception e ) {
	log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}

