package mx.finerio.api.aop

import mx.finerio.api.domain.*

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.security.core.userdetails.UserDetails 
import org.springframework.stereotype.Component

@Component
@Aspect
class MyUserDetailsServiceLoadUserByUsername {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.MyUserDetailsServiceLoadUserByUsername' )

  @Pointcut(
    value='execution(org.springframework.security.core.userdetails.UserDetails mx.finerio.api.services.MyUserDetailsService.loadUserByUsername(..)) && bean(myUserDetailsService) && args(username)',
    argNames='username'
  )
  public void loadUserByUsername( String username ) {}

  @Before('loadUserByUsername(username)')
  void before( String username ) {
    log.info( "<< username: {}", username )
  }

  @AfterReturning(
    pointcut='loadUserByUsername(String)',
    returning='response'
  )
  void afterReturning( UserDetails response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='loadUserByUsername(String)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
