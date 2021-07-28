package mx.finerio.api.aop

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
class CustomerLinkServiceFindOneByLinkId {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.CustomerLinkServiceFindOneByLinkId' )

    @Pointcut(
            value='execution(mx.finerio.api.domain.CustomerLink mx.finerio.api.services.CustomerLinkService.findOneByLinkId(..)) && bean(customerLinkService) && args(customerLinkId)',
            argNames='customerLinkId'
    )
    public void findOneByLinkId( String customerLinkId  ) {}

    @Before('findOneByLinkId(customerLinkId)')
    void before( String customerLinkId  ) {
        log.info( "<< customer: {}",customerLinkId )
    }

    @AfterReturning(
            pointcut='findOneByLinkId(String)',
            returning='response'
    )
    void afterReturning( CustomerLink response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='findOneByLinkId(String)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }
}

