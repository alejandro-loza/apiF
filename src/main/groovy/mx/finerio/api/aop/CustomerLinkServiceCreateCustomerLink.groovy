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
class CustomerLinkServiceCreateCustomerLink {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.CustomerLinkServiceCreateCustomerLink' )

    @Pointcut(
            value='execution(mx.finerio.api.domain.CustomerLink mx.finerio.api.services.CustomerLinkService.createCustomerLink(..)) && bean(customerLinkService) && args(customer,country)',
            argNames='customer,country'
    )
    public void createCustomerLink( Customer customer, Country country  ) {}

    @Before('createCustomerLink(customer,country)')
    void before( Customer customer, Country country  ) {
        log.info( "<< customer: {}, country: {}", customer,country )
    }

    @AfterReturning(
            pointcut='createCustomerLink(mx.finerio.api.domain.Customer,mx.finerio.api.domain.Country)',
            returning='response'
    )
    void afterReturning( CustomerLink response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='createCustomerLink(mx.finerio.api.domain.Customer,mx.finerio.api.domain.Country)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }
}

