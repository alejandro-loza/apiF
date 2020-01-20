package mx.finerio.api.aop 

import mx.finerio.api.domain.*
import mx.finerio.api.clients.*
import org.aspectj.lang.annotation.*
import org.slf4j.*
import org.springframework.stereotype.Component

@Component
@Aspect
class OauthClientDetailsServiceCreate {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.OauthClientDetailsServiceCreate' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.OauthClientDetailsService.create(..)) && bean(oauthClientDetailsService) && args(client)',
    argNames='client'
  )
  public void create( Client client ) {}

  @Before('create(client)')
  void before( Client client ) {
    log.info( "<< client: {}", client )
  }

  @AfterReturning(
    pointcut='create(mx.finerio.api.domain.Client)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='create(mx.finerio.api.domain.Client)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
