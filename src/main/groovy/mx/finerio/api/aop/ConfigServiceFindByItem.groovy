package mx.finerio.api.aop

import mx.finerio.api.domain.*

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
class ConfigServiceFindByItem {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ConfigServiceFindByItem' )

  @Pointcut(
    value='execution(String mx.finerio.api.services.ConfigService.findByItem(..)) && bean(configService) && args(item)',
    argNames='item'
  )
  public void findByItem(Config.Item item ) {}

  @Before('findByItem(item)')
  void before(Config.Item item ) {
    log.info( "<< item: {}", item )
  }

  @AfterReturning(
    pointcut='findByItem(mx.finerio.api.domain.Config.Item)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findByItem(mx.finerio.api.domain.Config.Item)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
