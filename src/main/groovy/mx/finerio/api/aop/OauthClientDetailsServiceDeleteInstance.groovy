package mx.finerio.api.aop 

import mx.finerio.api.domain.*
import mx.finerio.api.clients.*
import org.aspectj.lang.annotation.*
import org.slf4j.*
import org.springframework.stereotype.Component

@Component
@Aspect
class OauthClientDetailsServiceDeleteInstance {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.OauthClientDetailsServiceDeleteInstance' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.OauthClientDetailsService.deleteInstance(..)) && bean(oauthClientDetailsService) && args(client)',
    argNames='client'
  )
  public void deleteInstance( Client client ) {}

  @Before('deleteInstance(client)')
  void before( Client client ) {
    log.info( "<< client: {}", client )
  }

  @AfterReturning(
    pointcut='deleteInstance(mx.finerio.api.domain.Client)',
    returning='response'
  )
  void afterReturning() {
    log.info( '>> response: OK' )
  }

  @AfterThrowing(
    pointcut='deleteInstance(mx.finerio.api.domain.Client)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
