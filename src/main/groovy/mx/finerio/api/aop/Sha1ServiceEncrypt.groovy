package mx.finerio.api.aop

import javax.xml.bind.DatatypeConverter

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
class Sha1ServiceEncrypt {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.Sha1ServiceEncrypt' )

  @Pointcut(
    value='execution(mx.finerio.api.domain.Credential mx.finerio.api.services.Sha1Service.encrypt(..)) && bean(sha1Service) && args(input)',
    argNames='input'
  )
  public void encrypt( String input ) {}

  @Before('encrypt(input)')
  void before( String input ) {
    log.info( "<< input: {}", input )
  }

  @AfterReturning(
    pointcut='encrypt(String)',
    returning='response'
  )
  void afterReturning( byte[] response ) {
    log.info( '>> response: {}', DatatypeConverter.printHexBinary( response ) )
  }

  @AfterThrowing(
    pointcut='encrypt(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
