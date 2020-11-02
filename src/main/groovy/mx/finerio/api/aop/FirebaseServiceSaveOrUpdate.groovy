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
class FirebaseServiceSaveOrUpdate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.FirebaseServiceSaveOrUpdate' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.FirebaseService.saveOrUpdate(..)) && bean(firebaseService) && args(path, key, data)',
    argNames='path, key, data'
  )
  public void saveOrUpdate( String path, String key, Map data ) {}

  @Before('saveOrUpdate(path, key, data)')
  void before( String path, String key, Map data ) {
    log.info( "<< path: {}, key: {}, data: {}", path, key, data )
  }

  @AfterReturning(
    pointcut='saveOrUpdate(String, String, java.util.Map)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='saveOrUpdate(String, String, java.util.Map)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
