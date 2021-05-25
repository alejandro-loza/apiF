package mx.finerio.api.services

import mx.finerio.api.domain.Country
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.CustomerLink
import mx.finerio.api.domain.repository.CustomerLinkRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.utils.CommonUtils
import org.springframework.beans.factory.annotation.Autowired

class CustomerLinkService {

    @Autowired
    CustomerLinkRepository customerLinkRepository

    CustomerLink createCustomerLink( Customer customer, Country country ) throws Exception {
        def customerLink = new CustomerLink()
        customerLink.customer = customer
        customerLink.country = country
        customerLink.linkId = CommonUtils.createRandomString( 100 )
        return customerLinkRepository.save( customerLink )

    }

    CustomerLink findOne( String customerLinkId  ) throws Exception {

        if ( customerLinkId == null ) {
            throw new BadImplementationException(
                    'customerLinkService.findOne.customerLinkId.null' )
        }
        def instance = customerLinkRepository.findOneByIdAndDateDeletedIsNull( customerLinkId )
        if( !instance ){
            throw new InstanceNotFoundException( 'customerLink.not.found' )
        }
        instance
    }


    CustomerLink findOneByCustomerAndCountry(Customer customer, Country country ) throws Exception {

        if ( customer == null ) {
            throw new BadImplementationException(
                    'customerLinkService.findOneByCountryAndCustomer.customer.null' )
        }

        if ( country == null ) {
            throw new BadImplementationException(
                    'customerLinkService.findOneByCountryAndCustomer.country.null' )
        }

        def instance = customerLinkRepository.findOneByCustomerAndCountry(  customer, country )

        instance

    }

}