package mx.finerio.api.aop

import mx.finerio.api.domain.Account

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
class ClientConfigServiceGetCurrentApiKey {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.ClientConfigServiceGetCurrentApiKey' )

    @Pointcut(
            value='execution(java.lang.String mx.finerio.api.services.ClientConfigService.getCurrentApiKey(..)) && bean(clientConfigService)&& args(customerId)',
            argNames='customerId'
    )
    public void getCurrentApiKey( Long customerId ) {}

    @Before('getCurrentApiKey(customerId)')
    void before(  Long customerId  ) {
        log.info( "<< customerId: {}", customerId )
    }

    @AfterReturning(
            pointcut='getCurrentApiKey(Long)',
            returning='response'
    )
    void afterReturning( String response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='getCurrentApiKey(Long)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }

}
