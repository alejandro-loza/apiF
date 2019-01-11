package mx.finerio.api.aop

import java.util.Map

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
@Aspect
class CustomErrorMessageServiceSendCustomEmail {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.vest.aop.CustomErrorMessageServiceSendCustomEmail' )

  @Pointcut(
    value='execution(String mx.finerio.api.services.CustomErrorMessageService.sendCustomEmail(..)) && bean(customErrorMessageService) && args(emailId,statusCode)',
    argNames='emailId,statusCode'
  )
  public void sendCustomEmail( String emailId, String statusCode ) {}

  @Before('sendCustomEmail(emailId,statusCode)')
  void before( String emailId, String statusCode  ) {
    log.info( "<< emailId: {}, statusCode: {} ",emailId, statusCode )
  }

  @AfterReturning(
    pointcut='sendCustomEmail(String,String)',
    returning='response'
  )
  void afterReturning(String response) {
    log.info( "<< response: {}",response )
  }

  @AfterThrowing(
    pointcut='sendCustomEmail(String,String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
