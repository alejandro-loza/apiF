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
class MagicLinkServiceFindBanksByCustomerLinkId {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.MagicLinkServiceFindBanksByCustomerLinkId' )

    @Pointcut(
            value='execution(java.util.Map mx.finerio.api.services.MagicLinkService.findBanksByCustomerLinkId(..)) && bean(MagicLinkService) && args(customerLinkId)',
            argNames='customerLinkId'
    )
    public void findBanksByCustomerLinkId( String customerLinkId ) {}

    @Before('findBanksByCustomerLinkId(customerLinkId)')
    void before( String customerLinkId ) {
        log.info( "<< id: {}", customerLinkId )
    }

    @AfterReturning(
            pointcut='findBanksByCustomerLinkId(String)',
            returning='response'
    )
    void afterReturning( Map response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='findBanksByCustomerLinkId(String)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }


}
