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
            value='execution(java.lang.String mx.finerio.api.services.ClientConfigService.getCurrentApiKey(..)) && bean(clientConfigService)'
    )
    public void getCurrentApiKey() {}

    @Before('getCurrentApiKey()')
    void before(  ) {
        log.info( "<<" )
    }

    @AfterReturning(
            pointcut='getCurrentApiKey()',
            returning='response'
    )
    void afterReturning( String response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='getCurrentApiKey()',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }

}
