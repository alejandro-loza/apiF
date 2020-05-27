package mx.finerio.api.aop

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Callback

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
class AdminQueueServiceQueueMessage {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AdminQueueServiceQueueMessage' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.AdminQueueService.queueMessage(..)) && bean(adminQueueService) && args(data,label)',
    argNames='data,label'
  )
  public void queueMessage( Map data, String label  ) throws Exception {}

  @Before('queueMessage( data, label)')
  void before( Map data, String label ) {
    log.info( "<< data: {}, label: {}", data, label )
  }

  @AfterReturning(
    pointcut='queueMessage(java.util.Map, java.lang.String)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='queueMessage(java.util.Map, java.lang.String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
