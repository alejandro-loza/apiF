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
class MagicLinkServiceFindBankFieldsByCustomerLinkIdAndBankId {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.MagicLinkServiceFindBankFieldsByCustomerLinkIdAndBankId' )

    @Pointcut(
            value='execution(java.util.List mx.finerio.api.services.MagicLinkService.findBankFieldsByCustomerLinkIdAndBankId(..)) && bean(magicLinkService) && args(customerLinkId,bankId)',
            argNames='customerLinkId,bankId'
    )
    public void findBankFieldsByCustomerLinkIdAndBankId( String customerLinkId, Long bankId ) {}

    @Before('findBankFieldsByCustomerLinkIdAndBankId(customerLinkId,bankId)')
    void before( String customerLinkId, Long bankId ) {
        log.info( "<< customerLinkId {}, bankId: {}", customerLinkId,bankId )
    }

    @AfterReturning(
            pointcut='findBankFieldsByCustomerLinkIdAndBankId(String,Long)',
            returning='response'
    )
    void afterReturning( List response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='findBankFieldsByCustomerLinkIdAndBankId(String,Long)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }

}
