package mx.finerio.api.aop

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
class CallbackServiceFindAllByNature {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.CallbackServiceFindAllByNature' )

  @Pointcut(
    value='execution(java.util.List mx.finerio.api.services.CallbackService.findAllByNature(..)) && bean(callbackService) && args(nature)',
    argNames='nature'
  )
  public void findAllByNature( Callback.Nature nature ) {}

  @Before('findAllByNature(nature)')
  void before( Callback.Nature nature ) {
    log.info( "<< nature: {}", nature )
  }

  @AfterReturning(
    pointcut='findAllByNature(mx.finerio.api.domain.Callback.Nature)',
    returning='response'
  )
  void afterReturning( List response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findAllByNature(mx.finerio.api.domain.Callback.Nature)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
