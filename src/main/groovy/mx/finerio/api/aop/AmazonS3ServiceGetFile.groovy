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
class AmazonS3ServiceGetFile {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AmazonS3ServiceGetFile' )

  @Pointcut(
    value='execution(String mx.finerio.api.services.AmazonS3Service.getFile(..)) && bean(amazonS3Service) && args()',
    argNames=''
  )
  public void getFile() {}

  @Before('getFile()')
  void before( ) {
    log.info( "<< Start." )
  }

  @AfterReturning(
    pointcut='getFile()',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='getFile()',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
