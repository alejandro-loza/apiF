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
class CustomerLinkServiceFindOneByCustomerAndCountry {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.CustomerLinkServiceFindOneByCustomerAndCountry' )

    @Pointcut(
            value='execution(mx.finerio.api.domain.CustomerLink mx.finerio.api.services.CustomerLinkService.findOneByCustomerAndCountry(..)) && bean(customerLinkService) && args(customer,country)',
            argNames='customer,country'
    )
    public void findOneByCustomerAndCountry( Customer customer, Country country  ) {}

    @Before('findOneByCustomerAndCountry(customer,country)')
    void before( Customer customer, Country country  ) {
        log.info( "<< customer: {}, country: {}", customer,country )
    }

    @AfterReturning(
            pointcut='findOneByCustomerAndCountry(mx.finerio.api.domain.Customer,mx.finerio.api.domain.Country)',
            returning='response'
    )
    void afterReturning( CustomerLink response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='findOneByCustomerAndCountry(mx.finerio.api.domain.Customer,mx.finerio.api.domain.Country)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }
}

