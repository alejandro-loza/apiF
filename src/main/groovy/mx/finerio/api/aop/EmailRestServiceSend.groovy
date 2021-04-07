package mx.finerio.api.aop;

import java.util.Map

import mx.finerio.api.dtos.email.EmailSendDto

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
class EmailRestServiceSend {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.EmailRestServiceSend' )

  @Pointcut(
    value='execution(String mx.finerio.api.services.EmailRestService.send(..)) && bean(emailRestService) && args(dto)',
    argNames='dto'
  )
  public void send( EmailSendDto dto ) {}

  @Before('send(dto)')
  void before( EmailSendDto dto ) {
    log.info( "<< dto: {}", dto )
  }

  @AfterReturning(
    pointcut='send(mx.finerio.api.dtos.email.EmailSendDto)',
    returning='result'
  )
  void afterReturning( String result ) {
    log.info( ">> result: {}", result )
  }

  @AfterThrowing(
    pointcut='send(mx.finerio.api.dtos.email.EmailSendDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}

