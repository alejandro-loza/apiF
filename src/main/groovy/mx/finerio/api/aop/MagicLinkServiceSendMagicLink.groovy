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
class MagicLinkServiceSendMagicLink {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.MagicLinkServiceSendMagicLink' )

    @Pointcut(
            value='execution(void mx.finerio.api.services.MagicLinkService.sendMagicLink(..)) && bean(magicLinkService) && args(customerId)',
            argNames='customerId'
    )
    public void sendMagicLink( Long customerId ) {}

    @Before('sendMagicLink(customerId)')
    void before( Long customerId ) {
        log.info( "<< customerId: {} ", customerId )
    }

    @AfterReturning(
            pointcut='sendMagicLink(Long)'
    )
    void afterReturning() {
        log.info( '>> response: OK' )
    }

    @AfterThrowing(
            pointcut='sendMagicLink(Long)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }
}