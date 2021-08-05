package mx.finerio.api.aop

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
            value='execution(mx.finerio.api.domain.CustomerLink mx.finerio.api.services.CustomerLinkService.createCustomerLink(..)) && bean(customerLinkService) && args(customer)',
            argNames='customer'
    )
    public void createCustomerLink( Customer customer  ) {}

    @Before('createCustomerLink(customer)')
    void before( Customer customer) {
        log.info( "<< customer: {}", customer, )
    }

    @AfterReturning(
            pointcut='createCustomerLink(mx.finerio.api.domain.Customer)',
            returning='response'
    )
    void afterReturning( CustomerLink response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='createCustomerLink(mx.finerio.api.domain.Customer)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }
}

