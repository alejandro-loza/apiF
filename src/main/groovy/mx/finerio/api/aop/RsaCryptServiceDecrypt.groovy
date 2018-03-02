package mx.finerio.api.aop

import mx.finerio.api.domain.Customer

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
class RsaCryptServiceDecrypt {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.RsaCryptServiceDecrypt' )

  @Pointcut(
    value='execution(String mx.finerio.api.services.RsaCryptService.decrypt(..)) && bean(rsaCryptService) && args(cryptedText)',
    argNames='cryptedText'
  )
  public void decrypt( String cryptedText ) {}

  @Before('decrypt(cryptedText)')
  void before( String cryptedText ) {
    log.info( "<< cryptedText: {}", cryptedText )
  }

  @AfterReturning(
    pointcut='decrypt(String)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: ********' )
  }

  @AfterThrowing(
    pointcut='decrypt(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
