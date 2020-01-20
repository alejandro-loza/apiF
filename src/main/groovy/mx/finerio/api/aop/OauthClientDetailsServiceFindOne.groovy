package mx.finerio.api.aop 

import mx.finerio.api.domain.*
import mx.finerio.api.clients.*
import org.aspectj.lang.annotation.*
import org.slf4j.*
import org.springframework.stereotype.Component

@Component
@Aspect
class OauthClientDetailsServiceFindOne {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.OauthClientDetailsServiceFindOne' )

  @Pointcut(
    value='execution(java.util.Map mx.finerio.api.services.OauthClientDetailsService.findOne(..)) && bean(oauthClientDetailsService) && args(client)',
    argNames='client'
  )
  public void findOne( Client client ) {}

  @Before('findOne(client)')
  void before( Client client ) {
    log.info( "<< client: {}", client )
  }

  @AfterReturning(
    pointcut='findOne(mx.finerio.api.domain.Client)',
    returning='response'
  )
  void afterReturning( Map response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='findOne(mx.finerio.api.domain.Client)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
