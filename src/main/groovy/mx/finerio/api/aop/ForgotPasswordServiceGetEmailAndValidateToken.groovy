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
class ForgotPasswordServiceGetEmailAndValidateToken {

  final static Logger log = LoggerFactory.getLogger(
	  'mx.finerio.api.aop.ForgotPasswordServiceGetEmailAndValidateToken' )

  @Pointcut(
	value='execution(java.util.Map mx.finerio.api.services.ForgotPasswordService.getEmailAndvalidateToken(..)) && bean(forgotPasswordService) && args(token)',
	argNames='token'
  )
  public void getEmailAndvalidateToken( String token ) {}

  @Before('getEmailAndvalidateToken(token)')
  void before( String token ) {
	log.info( "<< token: {}", token )
  }

  @AfterReturning(
	pointcut='getEmailAndvalidateToken(java.lang.String)',
	returning='response'
  )
  void afterReturning( Map response ) {
	log.info( '>> response: {}', response )
  }

  @AfterThrowing(
	pointcut='getEmailAndvalidateToken(java.lang.String)',
	throwing='e'
  )
  void afterThrowing( Exception e ) {
	log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}

