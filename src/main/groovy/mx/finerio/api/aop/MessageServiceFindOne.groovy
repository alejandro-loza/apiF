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
class MessageServiceFindOne {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.MessageServiceFindOne' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.MessageService.findOne(..)) && bean(messageService) && args(message)',
    argNames='message'
  )
  public void findOne( String message ) {}

  @Before('findOne(message)')
  void before( String message ) {
    log.info( "<< id: {}", message )
  }

  @AfterReturning(
    pointcut='findOne(String)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOne(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.MessageService.findOne(..)) && bean(messageService) && args(message, code)',
    argNames='message, code'
  )
  public void findOne2( String message, String code ) {}

  @Before('findOne2(message, code)')
  void before2( String message, String code ) {
    log.info( "<< message: {}, code: {}", message, code )
  }

  @AfterReturning(
    pointcut='findOne2(String, String)',
    returning='response'
  )
  void afterReturning2( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOne2(String, String)',
    throwing='e'
  )
  void afterThrowing2( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
