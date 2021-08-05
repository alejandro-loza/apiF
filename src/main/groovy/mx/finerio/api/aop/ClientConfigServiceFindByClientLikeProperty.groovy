package mx.finerio.api.aop

import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.Client
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
class ClientConfigServiceFindByClientLikeProperty {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.ClientConfigServiceFindByClientLikeProperty' )

    @Pointcut(
            value='execution(java.util.List mx.finerio.api.services.ClientConfigService.findByClientLikeProperty(..)) && bean(clientConfigService) && args(client,property)',
            argNames='client,property'
    )
    public void findByClientLikeProperty( Client client, String property ) {}

    @Before('findByClientLikeProperty(client,property)')
    void before( Client client, String property ) {
        log.info( "<< client,property: {}", client,property )
    }

    @AfterReturning(
            pointcut='findByClientLikeProperty(mx.finerio.api.domain.Client,java.lang.String)',
            returning='response'
    )
    void afterReturning( List response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='findByClientLikeProperty(mx.finerio.api.domain.Client,java.lang.String)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }

}

