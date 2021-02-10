package mx.finerio.api.aop

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
class CallbackGatewayClientServiceRegisterCredential{

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CallbackGatewayClientServiceRegisterCredential' )

  @Pointcut(
    value='execution(java.lang.String mx.finerio.api.services.CallbackGatewayClientService.registerCredential(..)) && bean(callbackGatewayClientService) && args(data)',
    argNames='data'
  )
  public void registerCredential( Map data ) {}

  @Before('registerCredential(data)')
  void before(Map data) {
    log.info( "<< data: {}", data )
  }

  @AfterReturning(
    pointcut='registerCredential(java.util.Map)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='registerCredential(java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
