package mx.finerio.api.services

import mx.finerio.api.domain.Country
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.CustomerLink
import mx.finerio.api.domain.repository.CustomerLinkRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.utils.CommonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CustomerLinkService {

    @Autowired
    CustomerLinkRepository customerLinkRepository

    CustomerLink createCustomerLink( Customer customer ) throws Exception {
        def customerLink = new CustomerLink()
        customerLink.customer = customer
        customerLink.linkId = CommonUtils.createRandomString( 100 )
        customerLink.dateCreated = new Date()
        customerLink.lastUpdated = new Date()
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


    CustomerLink findOneByCustomer( Customer customer ) throws Exception {

        if ( customer == null ) {
            throw new BadImplementationException(
                    'customerLinkService.findOneByCountryAndCustomer.customer.null' )
        }

        def instance = customerLinkRepository.findOneByCustomer( customer )

        instance

    }

}