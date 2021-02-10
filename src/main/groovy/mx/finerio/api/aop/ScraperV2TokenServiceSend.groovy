package mx.finerio.api.aop

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component
import mx.finerio.api.dtos.CreateCredentialDto

@Component
@Aspect
class ScraperV2TokenServiceSend{

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperV2TokenServiceSend' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.ScraperV2TokenService.send(..)) && args(token,credentialId,bankCode)',
    argNames='token,credentialId,bankCode'
  )
  public void send( String token, String credentialId, String bankCode ) {}

  @Before('send(token,credentialId,bankCode)')
  void before( String token, String credentialId, String bankCode ) {
    log.info( "<< token: {},credentialId: {},bankCode: {}", token, credentialId, bankCode )
  }

  @AfterReturning(
    pointcut='send(String,String,String)'    
  )
  void afterReturning() {
    log.info( '>> ok')
  }

  @AfterThrowing(
    pointcut='send(String,String,String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
