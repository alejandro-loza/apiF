package mx.finerio.api.domain.repository

import mx.finerio.api.domain.Country
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.CustomerLink
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor



interface CustomerLinkRepository extends JpaRepository<CustomerLink, Long>,
        JpaSpecificationExecutor {

    CustomerLink findOneByCustomer( Customer customer)
    CustomerLink findOneByIdAndDateDeletedIsNull( Long id )
    CustomerLink findOneByLinkIdAndDateDeletedIsNull( String  linkId )


}
