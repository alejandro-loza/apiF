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
            value='execution(void mx.finerio.api.services.MagicLinkService.sendMagicLink(..)) && bean(magicLinkService) && args(customerId,countryId)',
            argNames='customerId,countryId'
    )
    public void sendMagicLink( Long customerId, String countryId ) {}

    @Before('sendMagicLink(customerId,countryId)')
    void before( Long customerId, String countryId ) {
        log.info( "<< customerId: {} countryId: {}", customerId,countryId )
    }

    @AfterReturning(
            pointcut='sendMagicLink(Long,String)'
    )
    void afterReturning() {
        log.info( '>> response: OK' )
    }

    @AfterThrowing(
            pointcut='sendMagicLink(Long,String)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }

}