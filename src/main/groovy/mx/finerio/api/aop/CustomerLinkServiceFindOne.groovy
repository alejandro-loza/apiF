package mx.finerio.api.aop

import mx.finerio.api.domain.Country
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.CustomerLink
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
class CustomerLinkServiceFindOne {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.CustomerLinkFindOne' )

    @Pointcut(
            value='execution(mx.finerio.api.domain.CustomerLink mx.finerio.api.services.CustomerLinkService.findOne(..)) && bean(customerLinkService) && args(customerLinkId)',
            argNames='customerLinkId'
    )
    public void findOne( String customerLinkId  ) {}

    @Before('findOne(customerLinkId)')
    void before( String customerLinkId  ) {
        log.info( "<< customer: {}",customerLinkId )
    }

    @AfterReturning(
            pointcut='findOne(String)',
            returning='response'
    )
    void afterReturning( CustomerLink response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='findOne(String)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }
}

